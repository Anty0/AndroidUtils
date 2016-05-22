package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Objects;
import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;

public final class CustomItemUtils {

    private static final String LOG_TAG = "CustomItemUtils";

    private CustomItemUtils() {
    }

    public static boolean usesCardView(CustomItem item) {
        return item.getClass().isAnnotationPresent(WrapWithCardView.class);
    }

    public static CustomItemViewSetup apply(@NonNull CustomItem item) {
        return new CustomItemViewSetup(item);
    }

    public static class CustomItemViewSetup {

        private static final String LOG_TAG = CustomItemUtils.LOG_TAG + "$CustomItemViewSetup";
        private static final String VIEW_TAG_KEY_THIS_IS_CONTENT = LOG_TAG + ".THIS_IS_CONTENT";
        private static final String VIEW_TAG_KEY_USING_CARD_VIEW = LOG_TAG + ".USING_CARD_VIEW";
        private static final String VIEW_TAG_KEY_LAYOUT_ID = LOG_TAG + ".LAYOUT_ID";

        private final CustomItem mItem;
        private int mPosition = CustomItem.NO_POSITION;
        private boolean mSupportsClicks = true;
        private Boolean mForceUseCardView;

        private CustomItemViewSetup(@NonNull CustomItem item) {
            mItem = item;
        }

        public CustomItem getItem() {
            return mItem;
        }

        public int getItemPosition() {
            return mPosition;
        }

        public CustomItemViewSetup withPosition(int position) {
            mPosition = position;
            return this;
        }

        public boolean isSupportsClicks() {
            return mSupportsClicks;
        }

        public void setSupportsClicks(boolean supportsClicks) {
            this.mSupportsClicks = supportsClicks;
        }

        public CustomItemViewSetup withClickSupport(boolean support) {
            mSupportsClicks = support;
            return this;
        }

        public CustomItemViewSetup withoutClickSupport() {
            mSupportsClicks = false;
            return this;
        }

        public CustomItemViewSetup withoutForceUseCardView() {
            mForceUseCardView = null;
            return this;
        }

        public CustomItemViewSetup withForceUseCardView() {
            mForceUseCardView = true;
            return this;
        }

        public CustomItemViewSetup withForceNotUseCardView() {
            mForceUseCardView = false;
            return this;
        }

        public View on(Context context, @Nullable ViewGroup parent) {
            return on(context, parent, null);
        }

        public View on(final Context context, @Nullable ViewGroup parent, @Nullable View oldView) {
            boolean useCardView = mForceUseCardView != null ? mForceUseCardView : usesCardView(mItem);
            oldView = createContent(context, parent, oldView, useCardView);
            ViewGroup content = (ViewGroup) Utils.findViewWithTag(oldView, VIEW_TAG_KEY_THIS_IS_CONTENT);

            View view = createItemView(context, content,
                    content.getChildCount() == 1 ? content.getChildAt(0) : null);

            content.removeAllViews();
            if (view != null) {
                content.addView(view);
                Utils.copyLayoutParamsToViewParents(view, oldView);
            }
            return oldView;
        }

        @NonNull
        private View createContent(Context context, @Nullable ViewGroup parent,
                                   @Nullable View oldContent, boolean useCardView) {
            // TODO: 22.5.16 find way to better working with CardView (with only one dependencies on layout id)
            if (oldContent != null && useCardView != (boolean)
                    Utils.getViewTagFromChildren(oldContent, VIEW_TAG_KEY_USING_CARD_VIEW))
                oldContent = null;

            if (oldContent == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                ViewGroup content;
                if (useCardView) {
                    oldContent = inflater.inflate(R.layout.card_view_base, parent, false);
                    content = (ViewGroup) oldContent.findViewById(R.id.card_view);
                } else {
                    oldContent = inflater.inflate(R.layout.frame_wrapper_base, parent, false);
                    content = (ViewGroup) oldContent.findViewById(R.id.frame_wrapper_content);
                }
                Utils.setViewTag(content, VIEW_TAG_KEY_THIS_IS_CONTENT, null);
                Utils.setViewTag(content, VIEW_TAG_KEY_USING_CARD_VIEW, useCardView);
            }
            return oldContent;
        }

        @Nullable
        private View createItemView(final Context context, @NonNull ViewGroup parent, @Nullable View oldView) {
            Object usedLayoutId = Utils.getViewTag(parent, VIEW_TAG_KEY_LAYOUT_ID);
            int requestedLayoutId = mItem.getLayoutResId(context, mPosition);
            if (oldView != null && (usedLayoutId == null || requestedLayoutId == CustomItem
                    .NO_LAYOUT_RES_ID || !Objects.equals(usedLayoutId, requestedLayoutId)))
                oldView = null;

            oldView = mItem.getView(context, parent, oldView, mPosition);

            if (oldView != null) {
                if (mSupportsClicks) {
                    if (mItem instanceof ClickableCustomItem) {
                        oldView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((ClickableCustomItem) mItem).onClick(context, v, mPosition);
                            }
                        });
                        oldView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return ((ClickableCustomItem) mItem).onLongClick(context, v, mPosition);
                            }
                        });
                    } else {
                        oldView.setOnClickListener(null);
                        oldView.setOnLongClickListener(null);
                    }
                }
            }

            // TODO: 25.3.16 find way to check if item gives true layout id
            Utils.setViewTag(parent, VIEW_TAG_KEY_LAYOUT_ID,
                    oldView != null ? requestedLayoutId : null);
            return oldView;
        }
    }

}

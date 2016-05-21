package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;

public final class CustomItemUtils {

    private static final String LOG_TAG = "CustomItemUtils";

    private CustomItemUtils() {
    }

    public static CustomItemViewSetup apply(@Nullable CustomItem item) {
        return new CustomItemViewSetup(item);
    }

    public static class CustomItemViewSetup {

        private final CustomItem mItem;
        private int mPosition = CustomItem.NO_POSITION;
        private boolean mSupportsClicks = true;
        private Boolean mForceUseCardView;

        private CustomItemViewSetup(@Nullable CustomItem item) {
            mItem = item == null ? new NullItem() : item;
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

        public View on(final Context context, @Nullable ViewGroup parent) {
            return on(context, parent, null);
        }

        public View on(final Context context, @Nullable ViewGroup parent, @Nullable View oldView) {
            boolean useCardView = mForceUseCardView != null ? mForceUseCardView :
                    mItem.getClass().isAnnotationPresent(WrapWithCardView.class);

            if (oldView == null) oldView = LayoutInflater.from(context).inflate(useCardView
                    ? R.layout.card_view_base : R.layout.frame_wrapper_base, parent, false);
            ViewGroup itemParent = (ViewGroup) oldView;
            if (useCardView != itemParent instanceof CardView)
                return on(context, parent, null);

            Object tag = itemParent.getTag();
            int layoutId = mItem.getLayoutResId(context, mPosition);
            View view = tag != null && layoutId != CustomItem.NO_LAYOUT_RES_ID
                    && (int) tag == layoutId ? itemParent.getChildAt(0) : null;

            itemParent.removeAllViews();
            view = mItem.getView(context, itemParent, view, mPosition);

            if (mSupportsClicks) {
                if (mItem instanceof ClickableCustomItem) {
                    itemParent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((ClickableCustomItem) mItem).onClick(context, v, mPosition);
                        }
                    });
                    itemParent.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return ((ClickableCustomItem) mItem).onLongClick(context, v, mPosition);
                        }
                    });
                } else {
                    itemParent.setOnClickListener(null);
                    itemParent.setOnLongClickListener(null);
                }
            }

            // TODO: 25.3.16 find way to check if item gives true layout id
            if (view != null) {
                itemParent.setTag(layoutId);
                itemParent.addView(view);
                Utils.copyLayoutParamsSizesToView(itemParent, view.getLayoutParams());
            } else itemParent.setTag(null);
            return oldView;
        }

        private static class NullItem implements CustomItem {

            @Override
            @Nullable
            public View getView(Context context, ViewGroup parent,
                                @Nullable View oldView, int itemPosition) {
                return null;
            }

            @Override
            public int getLayoutResId(Context context, int itemPosition) {
                return NO_LAYOUT_RES_ID;
            }
        }
    }

}

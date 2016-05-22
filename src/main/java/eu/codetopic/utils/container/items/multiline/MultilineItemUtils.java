package eu.codetopic.utils.container.items.multiline;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eu.codetopic.utils.Objects;
import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.callback.ActionCallback;

public final class MultilineItemUtils {

    private static final String LOG_TAG = "MultilineItemUtils";
    private static final String VIEW_TAG_KEY_VIEW_HOLDER = LOG_TAG + ".VIEW_HOLDER";

    private MultilineItemUtils() {
    }

    public static ItemViewHolder getItemViewHolderFor(MultilineItem item, View view,
                                                      @LayoutRes int layoutResourceId) {

        Object tag = Utils.getViewTag(view, VIEW_TAG_KEY_VIEW_HOLDER);
        ItemViewHolder holder;
        if (tag instanceof ItemViewHolder) {
            holder = (ItemViewHolder) tag;
            if (!Objects.equals(holder.layoutResourceId, layoutResourceId))
                throw new IllegalArgumentException("Wrong layout used for " + item.getClass().getName()
                        + ": holderLayoutId: " + holder.layoutResourceId + ", requiredLayoutId: " + layoutResourceId);
        } else {
            holder = new ItemViewHolder(view, layoutResourceId);
            Utils.setViewTag(view, VIEW_TAG_KEY_VIEW_HOLDER, holder);
        }
        return holder;
    }

    @LayoutRes
    public static int getLayoutResIdFor(Context context, MultilineItem item, int itemPosition,
                                        @Nullable @LayoutRes Integer defaultLayoutRes) {

        return item instanceof MultilineResourceLayoutItem ? ((MultilineResourceLayoutItem) item)
                .getLayoutResourceId(context, itemPosition) : (defaultLayoutRes == null ?
                MultilineItem.DEFAULT_ITEM_LAYOUT_ID : defaultLayoutRes);
    }

    public static MultilineItemViewSetup apply(@Nullable MultilineItem item) {
        return new MultilineItemViewSetup(item);
    }

    public interface ItemSetupCallback {

        void onImageLoaded(MultilineItem item, ItemViewHolder viewHolder,
                           @Nullable Bitmap bitmap, @Nullable Integer drawableResId);

        void onTextLoaded(MultilineItem item, ItemViewHolder viewHolder,
                          @Nullable CharSequence title, @Nullable CharSequence text);
    }

    public static class MultilineItemViewSetup {

        private final MultilineItem mItem;
        private int mPosition = MultilineItem.NO_POSITION;
        private ItemSetupCallback mCallback = null;
        private Integer mDefaultLayoutResId = MultilineItem.DEFAULT_ITEM_LAYOUT_ID;
        private boolean mUsePadding = true;

        private MultilineItemViewSetup(@Nullable MultilineItem item) {
            mItem = item == null ? new NullItem() : item;
        }

        public MultilineItem getItem() {
            return mItem;
        }

        @Nullable
        public ItemSetupCallback getCallback() {
            return mCallback;
        }

        public MultilineItemViewSetup withCallback(ItemSetupCallback callback) {
            mCallback = callback;
            return this;
        }

        public int getItemPosition() {
            return mPosition;
        }

        public MultilineItemViewSetup withPosition(int position) {
            mPosition = position;
            return this;
        }

        @Nullable
        @LayoutRes
        public Integer getDefaultLayoutResId() {
            return mDefaultLayoutResId;
        }

        public int getLayoutResId(Context context) {
            return getLayoutResIdFor(context, mItem, mPosition, mDefaultLayoutResId);
        }

        public MultilineItemViewSetup withDefaultLayoutResId(@LayoutRes Integer defaultLayoutResId) {
            mDefaultLayoutResId = defaultLayoutResId;
            return this;
        }

        public boolean isUsePadding() {
            return mUsePadding;
        }

        public MultilineItemViewSetup withPadding() {
            mUsePadding = true;
            return this;
        }

        public MultilineItemViewSetup withoutPadding() {
            mUsePadding = false;
            return this;
        }

        public View on(Context context, ViewGroup parent, @Nullable View oldView) {
            return on(LayoutInflater.from(context), parent, oldView);
        }

        public View on(LayoutInflater inflater, ViewGroup parent, @Nullable View oldView) {
            if (oldView != null) {
                try {
                    getItemViewHolderFor(mItem, oldView,
                            getLayoutResId(inflater.getContext()));
                } catch (Exception e) {
                    oldView = null;
                }
            }

            if (oldView == null) return on(inflater, parent, false);
            on(oldView);
            return oldView;
        }

        @MainThread
        public View on(Context context, ViewGroup parent, boolean attachToParent) {
            return on(LayoutInflater.from(context), parent, attachToParent);
        }

        @MainThread
        public View on(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
            int layoutRes = getLayoutResId(inflater.getContext());
            View view = inflater.inflate(layoutRes, parent, attachToParent);
            on(getItemViewHolderFor(mItem, view, layoutRes));
            return view;
        }

        @MainThread
        public void on(View view) {
            on(getItemViewHolderFor(mItem, view, getLayoutResId(view.getContext())));
        }

        @MainThread
        public void on(ItemViewHolder holder) {
            initImageIcon(holder);

            if (holder.text1 != null && holder.text2 != null) {
                holder.text1.setText(mItem.getTitle(holder.text1.getContext(), mPosition));
                CharSequence text = mItem.getText(holder.text1.getContext(), mPosition);
                if (text == null) {
                    setPaddingFor(holder.text1);
                    holder.text2.setVisibility(View.GONE);
                } else {
                    Utils.setPadding(holder.text1, 1, 1, 1, 1);
                    holder.text2.setVisibility(View.VISIBLE);
                    holder.text2.setText(text);
                }
            } else {
                TextView textView = holder.text1 != null ? holder.text1 : holder.text2;
                if (textView != null) {
                    textView.setText(holder.text1 != null ?
                            mItem.getTitle(textView.getContext(), mPosition) :
                            mItem.getText(textView.getContext(), mPosition));

                    setPaddingFor(textView);
                }
            }
            if (mCallback != null)
                mCallback.onTextLoaded(mItem, holder, holder.text1 == null ? null : holder.text1.getText(),
                        holder.text2 == null ? null : holder.text2.getText());
        }

        @MainThread
        protected void setPaddingFor(TextView textView) {
            Utils.setPadding(textView, 1, mUsePadding ? 8 : 1);
        }

        @MainThread
        private void initImageIcon(final ItemViewHolder holder) {
            if (holder.imgIcon == null) {
                if (mCallback != null) mCallback.onImageLoaded(mItem, null, null, null);
            } else if (mItem instanceof MultilineLoadableImageItem) {
                ((MultilineLoadableImageItem) mItem).loadImage(holder.imgIcon.getContext(),
                        mPosition, new ActionCallback<Bitmap>() {
                            @Override
                            public void onActionCompleted(@Nullable Bitmap result, @Nullable Throwable caughtThrowable) {
                                if (result == null) {
                                    initImageIconNoBackground(holder);
                                    return;
                                }
                                holder.imgIcon.setVisibility(View.VISIBLE);
                                holder.imgIcon.setImageBitmap(result);
                                if (mCallback != null)
                                    mCallback.onImageLoaded(mItem, holder, result, null);
                            }
                        });
            } else initImageIconNoBackground(holder);
        }

        @MainThread
        private void initImageIconNoBackground(ItemViewHolder holder) {
            if (holder.imgIcon == null) {
                if (mCallback != null) mCallback.onImageLoaded(mItem, holder, null, null);
            } else if (mItem instanceof MultilineImageItem) {
                Bitmap bitmap = ((MultilineImageItem) mItem)
                        .getImageBitmap(holder.imgIcon.getContext(), mPosition);
                if (bitmap != null) {
                    holder.imgIcon.setVisibility(View.VISIBLE);
                    holder.imgIcon.setImageBitmap(bitmap);
                }
                if (mCallback != null) mCallback.onImageLoaded(mItem, holder, bitmap, null);
            } else if (mItem instanceof MultilineResourceImageItem) {
                holder.imgIcon.setVisibility(View.VISIBLE);
                int imageResId = ((MultilineResourceImageItem) mItem)
                        .getImageResourceId(holder.imgIcon.getContext(), mPosition);
                holder.imgIcon.setImageResource(imageResId);
                if (mCallback != null) mCallback.onImageLoaded(mItem, holder, null, imageResId);
            } else {
                holder.imgIcon.setVisibility(View.GONE);
                if (mCallback != null) mCallback.onImageLoaded(mItem, holder, null, null);
            }
        }

        private static class NullItem implements MultilineItem {

            @Override
            public CharSequence getTitle(Context context, int position) {
                return null;
            }

            @Override
            public CharSequence getText(Context context, int position) {
                return null;
            }
        }
    }

    public static class ItemViewHolder {

        public final int layoutResourceId;
        public final ImageView imgIcon;
        public final TextView text1;
        public final TextView text2;

        ItemViewHolder(View view, @LayoutRes int layoutResourceId) {
            imgIcon = (ImageView) view.findViewById(R.id.image_view);
            text1 = (TextView) view.findViewById(R.id.text_view_title);
            text2 = (TextView) view.findViewById(R.id.text_view_text);
            this.layoutResourceId = layoutResourceId;
        }
    }

    public static abstract class SimpleItemSetupCallback implements ItemSetupCallback {

        @Override
        public void onImageLoaded(MultilineItem item, ItemViewHolder viewHolder,
                                  @Nullable Bitmap bitmap, @Nullable Integer drawableResId) {

        }

        @Override
        public void onTextLoaded(MultilineItem item, ItemViewHolder viewHolder,
                                 @Nullable CharSequence title, @Nullable CharSequence text) {

        }
    }

}

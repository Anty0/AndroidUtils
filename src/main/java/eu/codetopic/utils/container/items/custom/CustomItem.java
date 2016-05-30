package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.container.adapter.UniversalAdapter;

public abstract class CustomItem {

    public static final int NO_POSITION = -1;
    private static final String LOG_TAG = "CustomItem";
    private static final String VIEW_TAG_KEY_CONTENT_VIEW_HOLDER = LOG_TAG + ".WRAPPER_VIEW_HOLDER";

    private CustomItemWrapper[] wrappers;

    public CustomItem(@NonNull CustomItemWrapper... wrappers) {
        this.wrappers = wrappers;
    }

    public static ViewHolder createViewHolder(Context context, @Nullable ViewGroup parent,
                                              @LayoutRes int itemLayoutId) {
        return new ViewHolder(context, LayoutInflater.from(context)
                .inflate(itemLayoutId, parent, false), itemLayoutId);
    }

    public final ViewHolder createViewHolder(Context context, @Nullable ViewGroup parent) {
        return createViewHolder(context, parent, getLayoutResId(context));
    }

    public final void bindViewHolder(UniversalAdapter.ViewHolder holder, int itemPosition) {
        if (!(holder instanceof ViewHolder.UniversalHolder))
            throw new IllegalArgumentException("Invalid holder (unknown): " + holder);
        bindViewHolder(((ViewHolder.UniversalHolder) holder).getBase(), itemPosition);
    }

    public final void bindViewHolder(ViewHolder holder, int itemPosition) {
        performBindViewHolder(holder, itemPosition, null, new CustomItemWrapper[0]);
    }

    final ViewHolder performBindViewHolder(ViewHolder holder, int itemPosition,
                                           @Nullable CustomItem contentItem, CustomItemWrapper[] wrappers) {
        wrappers = Arrays.concat(getWrappers(holder.context), wrappers);
        if (wrappers.length > 0) holder = wrappers[0].performBindViewHolder(holder,
                itemPosition, this, Arrays.remove(wrappers, 0));

        if (holder.layoutResId != getItemLayoutResId(holder.context))
            throw new IllegalArgumentException("Invalid holder (wrong layout): " + holder);

        ViewHolder contentHolder = null;
        if (this instanceof CustomItemWrapper) {
            @IdRes int contentId = ((CustomItemWrapper) this).getContentViewId(holder.context);
            ViewGroup content = (ViewGroup) holder.itemView.findViewById(contentId);
            contentHolder = (ViewHolder) Utils.getViewTag(content, VIEW_TAG_KEY_CONTENT_VIEW_HOLDER);

            if (contentItem == null || (contentHolder != null && contentHolder.layoutResId
                    != contentItem.getItemLayoutResId(contentHolder.context))) {
                Utils.setViewTag(content, VIEW_TAG_KEY_CONTENT_VIEW_HOLDER, null);
                content.removeAllViews();
                contentHolder = null;
            }
            if (contentItem != null && contentHolder == null) {
                contentHolder = createViewHolder(holder.context, content,
                        contentItem.getItemLayoutResId(holder.context));
                content.addView(contentHolder.itemView);
                Utils.setViewTag(content, VIEW_TAG_KEY_CONTENT_VIEW_HOLDER, contentHolder);
            }
        }

        onBindViewHolder(holder, itemPosition);
        return contentHolder;
    }

    protected abstract void onBindViewHolder(ViewHolder holder, int itemPosition);

    @LayoutRes
    public final int getLayoutResId(Context context) {
        CustomItemWrapper[] wrappers = getWrappers(context);
        if (wrappers.length > 0) return wrappers[wrappers.length - 1].getLayoutResId(context);
        return getItemLayoutResId(context);
    }

    @LayoutRes
    public abstract int getItemLayoutResId(Context context);

    public boolean usesWrapper(Context context, Class<? extends CustomItemWrapper> wrapperClass) {
        for (CustomItemWrapper wrapper : getWrappers(context))
            if (wrapper.getClass().equals(wrapperClass)) return true;
        return false;
    }

    public final void setWrappers(@NonNull CustomItemWrapper... wrappers) {
        this.wrappers = wrappers;
    }

    @NonNull
    protected final CustomItemWrapper[] getWrappers(Context context) {
        return wrappers;
    }

    public static final class ViewHolder {

        public final Context context;
        public final View itemView;
        @LayoutRes public final int layoutResId;
        private UniversalHolder universalHolder = null;

        private ViewHolder(Context context, View itemView, @LayoutRes int layoutResId) {
            this.context = context;
            this.itemView = itemView;
            this.layoutResId = layoutResId;
        }

        public UniversalAdapter.ViewHolder forUniversalAdapter() {
            if (universalHolder == null)
                universalHolder = new UniversalHolder(this);
            return universalHolder;
        }

        private static class UniversalHolder extends UniversalAdapter.ViewHolder {

            private final ViewHolder base;

            public UniversalHolder(ViewHolder base) {
                super(base.itemView, base.layoutResId);
                this.base = base;
            }

            public ViewHolder getBase() {
                return base;
            }
        }
    }

}

package eu.codetopic.utils.ui.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;

import eu.codetopic.utils.ui.container.adapter.UniversalAdapter;
import eu.codetopic.utils.ui.view.ViewUtils;

public abstract class CustomItem implements Serializable {

    public static final int NO_POSITION = -1;
    private static final String LOG_TAG = "CustomItem";
    private static final String VIEW_TAG_KEY_CONTENT_VIEW_HOLDER = LOG_TAG + ".WRAPPER_VIEW_HOLDER";

    public static ViewHolder createViewHolder(Context context, @Nullable ViewGroup parent,
                                              @LayoutRes int itemLayoutId) {
        return createViewHolder(context, parent, null, itemLayoutId);
    }

    private static ViewHolder createViewHolder(Context context, @Nullable ViewGroup parent,
                                               @Nullable ViewHolder parentHolder,
                                               @LayoutRes int itemLayoutId) {
        return new ViewHolder(context, LayoutInflater.from(context)
                .inflate(itemLayoutId, parent, false), parentHolder, itemLayoutId);
    }

    public final ViewHolder createViewHolder(Context context, @Nullable ViewGroup parent) {
        return createViewHolder(context, parent, getLayoutResId(context));
    }

    public final void bindViewHolder(UniversalAdapter.ViewHolder holder, int itemPosition) {
        if (!(holder instanceof ViewHolder.UniversalHolder))
            throw new IllegalArgumentException("Invalid holder (unknown holder): " + holder);
        bindViewHolder(((ViewHolder.UniversalHolder) holder).getBase(), itemPosition);
    }

    public final void bindViewHolder(ViewHolder holder, int itemPosition) {
        performBindViewHolder(holder, itemPosition, null, new CustomItemWrapper[0]);
    }

    final ViewHolder performBindViewHolder(ViewHolder holder, int itemPosition,
                                           @Nullable CustomItem contentItem, CustomItemWrapper[] wrappers) {
        wrappers = ArrayUtils.addAll(getWrappers(holder.context), wrappers);
        if (wrappers.length > 0) holder = wrappers[0].performBindViewHolder(holder,
                itemPosition, this, ArrayUtils.remove(wrappers, 0));

        if (holder.layoutResId != getItemLayoutResId(holder.context))
            throw new IllegalArgumentException("Invalid holder (wrong layout): " + holder);

        ViewHolder contentHolder = null;
        if (this instanceof CustomItemWrapper) {
            @IdRes int contentId = ((CustomItemWrapper) this).getContentViewId(holder.context);
            ViewGroup content = (ViewGroup) holder.itemView.findViewById(contentId);
            contentHolder = (ViewHolder) ViewUtils.getViewTag(content, VIEW_TAG_KEY_CONTENT_VIEW_HOLDER);

            if (contentItem == null || (contentHolder != null && contentHolder.layoutResId
                    != contentItem.getItemLayoutResId(contentHolder.context))) {
                ViewUtils.setViewTag(content, VIEW_TAG_KEY_CONTENT_VIEW_HOLDER, null);
                content.removeAllViews();
                contentHolder = null;
            }
            if (contentItem != null && contentHolder == null) {
                contentHolder = createViewHolder(holder.context, content, holder,
                        contentItem.getItemLayoutResId(holder.context));
                content.addView(contentHolder.itemView);
                ViewUtils.setViewTag(content, VIEW_TAG_KEY_CONTENT_VIEW_HOLDER, contentHolder);
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

    @NonNull
    protected CustomItemWrapper[] getWrappers(Context context) {
        return new CustomItemWrapper[0];
    }

    public static final class ViewHolder {

        public final Context context;
        public final View itemView;
        public final ViewHolder parentHolder;
        @LayoutRes public final int layoutResId;
        private UniversalHolder universalHolder = null;

        private ViewHolder(Context context, View itemView, @Nullable ViewHolder parentHolder,
                           @LayoutRes int layoutResId) {
            this.context = context;
            this.itemView = itemView;
            this.parentHolder = parentHolder;
            this.layoutResId = layoutResId;
        }

        @Nullable
        public ViewHolder getParentHolder() {
            return parentHolder;
        }

        public ViewHolder getTopParentHolder() {
            ViewHolder parentHolder = getParentHolder();
            if (parentHolder == null) return this;
            return parentHolder.getTopParentHolder();
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

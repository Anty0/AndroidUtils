package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public class LayoutItemWrapper extends CustomItemWrapper {

    private static final String LOG_TAG = "LayoutItemWrapper";

    @LayoutRes private final int layoutRes;
    @IdRes private final int contentViewId;

    public LayoutItemWrapper(@LayoutRes int layoutRes, @IdRes int contentViewId,
                             @NonNull CustomItemWrapper... wrappers) {
        super(wrappers);
        this.layoutRes = layoutRes;
        this.contentViewId = contentViewId;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return layoutRes;
    }

    @Override
    protected int getContentViewId(Context context) {
        return contentViewId;
    }
}

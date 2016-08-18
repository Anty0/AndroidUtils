package eu.codetopic.utils.ui.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public class LayoutItem extends CustomItem {

    private static final String LOG_TAG = "LayoutItem";

    @LayoutRes private final int layoutRes;
    private final CustomItemWrapper[] wrappers;

    public LayoutItem(@LayoutRes int layoutRes, @NonNull CustomItemWrapper... wrappers) {
        this.layoutRes = layoutRes;
        this.wrappers = wrappers;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return layoutRes;
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return wrappers;
    }
}

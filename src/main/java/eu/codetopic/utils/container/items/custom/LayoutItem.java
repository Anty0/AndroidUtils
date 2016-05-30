package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public class LayoutItem extends CustomItem {

    private static final String LOG_TAG = "LayoutItem";

    @LayoutRes private final int layoutRes;

    public LayoutItem(@LayoutRes int layoutRes, @NonNull CustomItemWrapper... wrappers) {
        super(wrappers);
        this.layoutRes = layoutRes;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return layoutRes;
    }
}

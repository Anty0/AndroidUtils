package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LayoutItem implements CustomItem {

    private static final String LOG_TAG = "LayoutItem";

    @LayoutRes private final int layoutRes;

    public LayoutItem(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
    }

    @Nullable
    @Override
    public View getView(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        if (oldView == null) oldView = LayoutInflater.from(context)
                .inflate(getLayoutResId(context, itemPosition), parent, false);
        return oldView;
    }

    @Override
    public int getLayoutResId(Context context, int itemPosition) {
        return layoutRes;
    }
}

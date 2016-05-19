package eu.codetopic.utils.container.items.cardview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.container.items.custom.CustomItem;

public abstract class CardItem implements CustomItem {

    @Nullable
    @Override
    public final View getView(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        if (oldView == null) oldView = LayoutInflater.from(context)
                .inflate(getLayoutResId(context, itemPosition), parent, false);

        ViewGroup itemParent = (ViewGroup) oldView.findViewById(R.id.card_view);

        Object tag = itemParent.getTag();
        int layoutId = getBaseLayoutResId(context, itemPosition);
        View view = tag != null && layoutId != CardItem.NO_LAYOUT_RES_ID
                && (int) tag == layoutId ? itemParent.getChildAt(0) : null;

        itemParent.removeAllViews();
        view = getViewBase(context, itemParent, view, itemPosition);

        // TODO: 25.3.16 find way to check if item gives true layout id
        if (view != null) {
            itemParent.setTag(layoutId);
            itemParent.addView(view);
        } else itemParent.setTag(null);
        return oldView;
    }


    @Override
    @LayoutRes
    public final int getLayoutResId(Context context, int itemPosition) {
        return R.layout.card_view_base;
    }

    @Nullable
    protected abstract View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition);

    @LayoutRes
    protected abstract int getBaseLayoutResId(Context context, int itemPosition);
}

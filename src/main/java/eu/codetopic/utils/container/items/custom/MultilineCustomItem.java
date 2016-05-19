package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.MultilineItemUtils;

public abstract class MultilineCustomItem implements CustomItem, MultilineItem {

    private static final String LOG_TAG = "MultilineCustomItem";

    @Override
    public View getView(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        return MultilineItemUtils.apply(this).withoutPadding()
                .withDefaultLayoutResId(getLayoutResId(context, itemPosition))
                .withPosition(itemPosition).on(context, parent, oldView);
    }

    @Override
    @LayoutRes
    public final int getLayoutResId(Context context, int itemPosition) {
        return MultilineItemUtils.getLayoutResIdFor(context, this, itemPosition, null);
    }

}

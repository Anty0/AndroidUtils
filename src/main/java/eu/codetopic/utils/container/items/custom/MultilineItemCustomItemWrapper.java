package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.MultilineItemUtils;

public class MultilineItemCustomItemWrapper implements CustomItem {

    private static final String LOG_TAG = "MultilineItemCustomItemWrapper";

    private final MultilineItem item;

    public MultilineItemCustomItemWrapper(MultilineItem item) {
        this.item = item;
    }

    public static List<MultilineItemCustomItemWrapper> wrapAll(Collection<? extends MultilineItem> items) {
        List<MultilineItemCustomItemWrapper> wrappedItems = new ArrayList<>();
        for (MultilineItem item : items)
            wrappedItems.add(new MultilineItemCustomItemWrapper(item));
        return wrappedItems;
    }

    public MultilineItem getItem() {
        return item;
    }

    @Override
    public View getView(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        return MultilineItemUtils.apply(item).withoutPadding()
                .withDefaultLayoutResId(getLayoutResId(context, itemPosition))
                .withPosition(itemPosition).on(context, parent, oldView);
    }

    @Override
    @LayoutRes
    public int getLayoutResId(Context context, int itemPosition) {
        return MultilineItemUtils.getLayoutResIdFor(context, item, itemPosition, null);
    }


}

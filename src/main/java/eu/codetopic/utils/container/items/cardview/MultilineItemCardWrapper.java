package eu.codetopic.utils.container.items.cardview;

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

public class MultilineItemCardWrapper extends CardItemNoClickImpl {

    private final MultilineItem item;

    public MultilineItemCardWrapper(MultilineItem item) {
        this.item = item;
    }

    public static List<MultilineItemCardWrapper> wrapAll(Collection<? extends MultilineItem> items) {
        List<MultilineItemCardWrapper> wrappedItems = new ArrayList<>();
        for (MultilineItem item : items)
            wrappedItems.add(new MultilineItemCardWrapper(item));
        return wrappedItems;
    }

    public MultilineItem getItem() {
        return item;
    }

    @Override
    public View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        return MultilineItemUtils.apply(item).withoutPadding()
                .withDefaultLayoutResId(getLayoutResId(context, itemPosition))
                .withPosition(itemPosition).on(context, parent, oldView);
    }

    @Override
    @LayoutRes
    public int getBaseLayoutResId(Context context, int itemPosition) {
        return MultilineItemUtils.getLayoutResIdFor(context, item, itemPosition, null);
    }

}
package cz.codetopic.utils.list.items.cardview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.codetopic.utils.list.items.multiline.MultilineItem;
import cz.codetopic.utils.list.items.multiline.MultilineItemUtils;
import cz.codetopic.utils.list.items.multiline.MultilineResourceLayoutItem;

/**
 * Created by anty on 30.3.16.
 *
 * @author anty
 */
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
        if (oldView != null) {
            MultilineItemUtils.applyMultilineItemOnView(oldView, item);
            return oldView;
        }
        return MultilineItemUtils.applyMultilineItemOnView(context,
                parent, item, getLayoutResId(context, itemPosition));
    }

    @Override
    public int getLayoutResId(Context context, int itemPosition) {
        return item instanceof MultilineResourceLayoutItem ? ((MultilineResourceLayoutItem) item)
                .getLayoutResourceId(context, itemPosition) : MultilineItem.DEFAULT_ITEM_LAYOUT_ID;
    }

}

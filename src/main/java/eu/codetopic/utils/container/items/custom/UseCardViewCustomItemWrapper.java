package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by anty on 21.5.16.
 *
 * @author anty
 */
@WrapWithCardView
public class UseCardViewCustomItemWrapper implements ClickableCustomItem {

    private final CustomItem item;

    public UseCardViewCustomItemWrapper(CustomItem item) {
        this.item = item;
    }

    public static List<UseCardViewCustomItemWrapper> wrapAll(Collection<? extends CustomItem> items) {
        List<UseCardViewCustomItemWrapper> wrappedItems = new ArrayList<>();
        for (CustomItem item : items)
            wrappedItems.add(new UseCardViewCustomItemWrapper(item));
        return wrappedItems;
    }

    @Nullable
    @Override
    public View getView(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        return item.getView(context, parent, oldView, itemPosition);
    }

    @Override
    public int getLayoutResId(Context context, int itemPosition) {
        return item.getLayoutResId(context, itemPosition);
    }

    @Override
    public void onClick(Context context, View v, int itemPosition) {
        if (item instanceof ClickableCustomItem)
            ((ClickableCustomItem) item).onClick(context, v, itemPosition);
    }

    @Override
    public boolean onLongClick(Context context, View v, int itemPosition) {
        return item instanceof ClickableCustomItem &&
                ((ClickableCustomItem) item).onLongClick(context, v, itemPosition);
    }
}

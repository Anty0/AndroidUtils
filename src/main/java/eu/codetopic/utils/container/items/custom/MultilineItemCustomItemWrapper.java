package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.MultilineItemUtils;

/**
 * Created by anty on 23.5.16.
 *
 * @author anty
 */
public class MultilineItemCustomItemWrapper extends CustomItem {

    private static final String LOG_TAG = "MultilineItemCustomItemWrapper";

    private final MultilineItem item;
    private final CustomItemWrapper[] wrappers;

    public MultilineItemCustomItemWrapper(@Nullable MultilineItem item, @NonNull CustomItemWrapper... wrappers) {
        if (item == null && this instanceof MultilineItem) this.item = (MultilineItem) this;
        else this.item = item;
        this.wrappers = wrappers;
    }

    public static List<MultilineItemCustomItemWrapper> wrapAll(Collection<? extends MultilineItem> items) {
        List<MultilineItemCustomItemWrapper> wrappedItems = new ArrayList<>();
        for (MultilineItem item : items)
            wrappedItems.add(new MultilineItemCustomItemWrapper(item));
        return wrappedItems;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        MultilineItemUtils.apply(item).withoutPadding()
                .withDefaultLayoutResId(getLayoutResId(holder.context))
                .withPosition(itemPosition).on(holder.itemView);
    }

    @Override
    @LayoutRes
    public int getItemLayoutResId(Context context) {
        return MultilineItemUtils.getLayoutResIdFor(context, item, null);
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return wrappers;
    }
}

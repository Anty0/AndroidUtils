package eu.codetopic.utils.container.adapter.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;

import eu.codetopic.utils.container.items.custom.CustomItem;

public class SimpleItemInfo extends ItemInfo {

    private static final String LOG_TAG = "SimpleItemInfo";
    private final CustomItem item;
    private final int priority;

    public SimpleItemInfo(CustomItem item, int priority) {
        this.item = item;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @NonNull
    @Override
    public CustomItem getItem(Context context) {
        return item;
    }
}

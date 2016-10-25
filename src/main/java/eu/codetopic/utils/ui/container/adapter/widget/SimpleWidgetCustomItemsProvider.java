package eu.codetopic.utils.ui.container.adapter.widget;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;

import eu.codetopic.utils.ui.container.items.custom.CustomItem;

public class SimpleWidgetCustomItemsProvider implements WidgetCustomItemsProvider {

    private static final String LOG_TAG = "SimpleWidgetCustomItemsProvider";

    @NonNull
    @Override
    public Collection<? extends CustomItem> getItems(Context context) throws Exception {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public IntentFilter getOnItemsChangedIntentFilter(Context context) {
        return null;
    }
}

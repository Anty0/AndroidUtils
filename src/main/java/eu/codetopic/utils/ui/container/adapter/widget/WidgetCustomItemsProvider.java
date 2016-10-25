package eu.codetopic.utils.ui.container.adapter.widget;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Collection;

import eu.codetopic.utils.ui.container.items.custom.CustomItem;

public interface WidgetCustomItemsProvider extends Serializable {

    @NonNull
    Collection<? extends CustomItem> getItems(Context context) throws Exception;

    @Nullable
    IntentFilter getOnItemsChangedIntentFilter(Context context);
}

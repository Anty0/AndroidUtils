/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.ui.container.adapter.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.ui.container.adapter.CustomItemAdapter;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;

public class CustomItemWidgetAdapter extends CustomItemAdapter<CustomItem> {

    private static final String LOG_TAG = "CustomItemWidgetAdapter";

    private final WidgetCustomItemsProvider itemsProvider;
    private final IntentFilter itemsChangedIntentFilter;
    private final BroadcastReceiver itemsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateItems();
        }
    };

    public CustomItemWidgetAdapter(Context context, WidgetCustomItemsProvider itemsProvider) {
        super(context);
        this.itemsProvider = itemsProvider;
        this.itemsChangedIntentFilter = itemsProvider.getOnItemsChangedIntentFilter(context);
    }

    public static Intent getServiceIntent(Context context, WidgetCustomItemsProvider itemsProvider) {
        return CustomItemWidgetAdapterService.getIntent(context, itemsProvider);
    }

    @Override
    public void onAttachToContainer(@Nullable Object container) {
        if (itemsChangedIntentFilter != null) {
            getContext().registerReceiver(itemsChangedReceiver, itemsChangedIntentFilter);
        }
        updateItems();
        super.onAttachToContainer(container);
    }

    protected void updateItems() {
        Editor<CustomItem> editor = edit().clear();
        try {
            editor.addAll(itemsProvider.getItems(getContext()));
        } catch (Exception e) {
            Log.e(LOG_TAG, "updateItems: from " + itemsProvider, e);
        } finally {
            editor.apply();
        }
    }

    @Override
    public void onDetachFromContainer(@Nullable Object container) {
        super.onDetachFromContainer(container);
        if (itemsChangedIntentFilter != null) {
            getContext().unregisterReceiver(itemsChangedReceiver);
        }
        edit().clear().apply();
    }
}

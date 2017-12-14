/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        Editor<CustomItem> editor = edit();
        editor.clear();
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
        Editor<CustomItem> editor = edit();
        editor.clear();
        editor.apply();
    }
}

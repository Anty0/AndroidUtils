/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.container.adapter.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViewsService;

import eu.codetopic.java.utils.log.Log;

@TargetApi(11)
public class CustomItemWidgetAdapterService extends RemoteViewsService {

    private static final String LOG_TAG = "CustomItemWidgetAdapterService";

    private static final String EXTRA_ITEMS_PROVIDER = "eu.codetopic.utils.ui.container.adapter.widget."
            + LOG_TAG + ".EXTRA_ITEMS_PROVIDER";

    public static Intent getIntent(Context context, WidgetCustomItemsProvider itemsProvider) {
        Intent intent = new Intent(context, CustomItemWidgetAdapterService.class)
                .putExtra(EXTRA_ITEMS_PROVIDER, itemsProvider);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetCustomItemsProvider itemsProvider = (WidgetCustomItemsProvider) intent.getSerializableExtra(EXTRA_ITEMS_PROVIDER);
        if (itemsProvider == null) {
            Log.e(LOG_TAG, "onGetViewFactory -> EXTRA_ITEMS_PROVIDER not found in intent, list will be empty");
            itemsProvider = new SimpleWidgetCustomItemsProvider();
        }
        return new CustomItemWidgetAdapter(this, itemsProvider).forWidget(this, null);
    }
}

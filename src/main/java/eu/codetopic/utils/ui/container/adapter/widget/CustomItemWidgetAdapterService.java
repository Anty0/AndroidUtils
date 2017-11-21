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

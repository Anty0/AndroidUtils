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

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

package eu.codetopic.utils.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.debug.items.ConnectivityDebugItem;
import eu.codetopic.utils.debug.items.LoggingDebugItem;
import eu.codetopic.utils.debug.items.TimedComponentsManagerDebugItem;
import eu.codetopic.utils.ui.activity.modular.module.BackButtonModule;
import eu.codetopic.utils.ui.activity.modular.module.ToolbarModule;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.recycler.Recycler;

public abstract class BaseDebugActivity extends ModularActivity {

    private static final String LOG_TAG = "BaseDebugActivity";

    public BaseDebugActivity() {
        super(new ToolbarModule(), new BackButtonModule());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<CustomItem> items = new ArrayList<>();
        prepareDebugItems(items);
        Recycler.inflate().on(this).setAdapter(items);
    }

    protected void prepareDebugItems(List<CustomItem> items) {
        items.add(new ConnectivityDebugItem());
        items.add(new TimedComponentsManagerDebugItem());
        items.add(new LoggingDebugItem());
        // add here Utils debug items
    }

}

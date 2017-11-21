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

package eu.codetopic.utils.context;

import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

@Deprecated
@SuppressWarnings("deprecation")
public class ActivityDestroyReporterModule extends SimpleActivityCallBackModule implements ActivityDestroyReporter {

    private static final String LOG_TAG = "ActivityDestroyReporterModule";

    private final DestroyReporterHelper helper = new DestroyReporterHelper();

    public synchronized void registerListener(ActivityDestroyListener listener) {
        helper.registerListener(listener);
    }

    public synchronized void unregisterListener(ActivityDestroyListener listener) {
        helper.unregisterListener(listener);
    }

    @Override
    protected synchronized void onDestroy() {
        helper.reportDestroy();
        super.onDestroy();
    }
}

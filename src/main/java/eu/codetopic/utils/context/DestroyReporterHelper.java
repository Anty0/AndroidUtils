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

import java.util.ArrayList;
import java.util.List;

@Deprecated
@SuppressWarnings("deprecation")
public class DestroyReporterHelper implements ActivityDestroyReporter {

    private static final String LOG_TAG = "DestroyReporterHelper";

    private final List<ActivityDestroyListener> mListeners = new ArrayList<>();

    private boolean destroyed = false;

    public synchronized void registerListener(ActivityDestroyListener listener) {
        if (destroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mListeners.add(listener);
    }

    public synchronized void unregisterListener(ActivityDestroyListener listener) {
        if (destroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mListeners.remove(listener);
    }

    public synchronized void reportDestroy() {
        for (ActivityDestroyListener listener : mListeners)
            listener.onDestroy();
        destroyed = true;
        mListeners.clear();
    }

}

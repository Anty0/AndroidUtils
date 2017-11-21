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

package eu.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.BuildConfig;
import eu.codetopic.utils.timing.info.TimCompInfo;

@MainThread
public class ReloadComponentReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "ReloadComponentReceiver";

    private static final String ACTION_RELOAD_COMPONENT =
            "eu.codetopic.utils.timing.TimedComponentsManager.RELOAD_COMPONENT";
    private static final String EXTRA_TIMED_COMPONENT_CLASS_NAME =
            "eu.codetopic.utils.timing.TimedComponentsManager.TIMED_COMPONENT_CLASS_NAME";

    static Intent generateIntent(Context context, @NonNull TimCompInfo componentInfo) {
        return new Intent(context, ReloadComponentReceiver.class)
                .setAction(ACTION_RELOAD_COMPONENT)
                .putExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME,// fixes api 24 class passing trough PendingIntent
                        componentInfo.getComponentClass().getName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), ACTION_RELOAD_COMPONENT)) return;
        if (!TimedComponentsManager.isInitialized()) return;
        Class<?> clazz;
        try {
            clazz = Class.forName(intent.getStringExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME));
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "onReceive: can't find requested class to reload", e);
            return;
        }

        if (BuildConfig.DEBUG) {
            TimingData.getter.get().addDebugLogLine(String.format(
                    "Received reload request of component %1$s", clazz.getName()));
        }

        TimedComponentsManager.getInstance().tryReload(clazz);
    }
}

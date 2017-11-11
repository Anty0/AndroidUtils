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

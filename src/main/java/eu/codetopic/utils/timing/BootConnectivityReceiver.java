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

package eu.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.annotation.MainThread;

import eu.codetopic.utils.BuildConfig;
import eu.codetopic.utils.timing.info.TimCompInfo;

@MainThread
public final class BootConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TimedComponentsManager.isInitialized()) return;
        TimedComponentsManager timCompsMan = TimedComponentsManager.getInstance();
        TimingData data = TimingData.getter.get();
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                if (BuildConfig.DEBUG) {
                    data.addDebugLogLine("Received boot completed");
                }

                synchronized (timCompsMan.getTimedComponentsLock()) {
                    for (TimCompInfo componentInfo : timCompsMan.getAllTimedComponentInfo())
                        if (componentInfo.getComponentProperties().isResetRepeatingOnBoot())
                            data.clear(componentInfo.getComponentClass());

                    timCompsMan.reloadAll();
                }
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
            case ConnectivityManager.CONNECTIVITY_ACTION:
                if (BuildConfig.DEBUG) {
                    data.addDebugLogLine("Received connectivity change");
                }

                timCompsMan.reloadAllNetwork();
                break;
        }
    }
}

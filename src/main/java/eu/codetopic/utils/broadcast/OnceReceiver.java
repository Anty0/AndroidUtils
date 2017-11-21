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

package eu.codetopic.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.broadcast.BroadcastsConnector.BroadcastTargetingType;

@UiThread
public abstract class OnceReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "OnceReceiver";

    private final BroadcastTargetingType target;

    public OnceReceiver(BroadcastTargetingType target, Context context, String action) {
        this(target, context, new IntentFilter(action));
    }

    public OnceReceiver(BroadcastTargetingType target, Context context, IntentFilter intentFilter) {
        this.target = target;
        switch (target) {
            case GLOBAL:
                context.registerReceiver(this, intentFilter);
                break;
            case LOCAL:
                LocalBroadcastManager.getInstance(context)
                        .registerReceiver(this, intentFilter);
                break;
            default:
                Log.e(LOG_TAG, "Detected problem in " + LOG_TAG
                        + ": can't recognise BroadcastTargetingType - " + target);
                break;
        }
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        switch (target) {
            case GLOBAL:
                context.unregisterReceiver(this);
                break;
            case LOCAL:
                LocalBroadcastManager.getInstance(context)
                        .unregisterReceiver(this);
                break;
            default:
                Log.e(LOG_TAG, "Detected problem in " + LOG_TAG
                        + ": can't recognise BroadcastTargetingType - " + target);
                break;
        }
        onReceived(context, intent);
    }

    public abstract void onReceived(Context context, Intent intent);
}

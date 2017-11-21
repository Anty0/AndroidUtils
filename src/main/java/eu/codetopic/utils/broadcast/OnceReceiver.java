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

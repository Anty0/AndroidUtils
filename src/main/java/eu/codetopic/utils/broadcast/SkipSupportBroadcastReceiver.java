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

public abstract class SkipSupportBroadcastReceiver extends BroadcastReceiver {

    private int toSkip = 0;

    public synchronized void skipNext() {
        toSkip++;
    }

    @Override
    public final synchronized void onReceive(Context context, Intent intent) {
        if (toSkip > 0) {
            toSkip--;
            onDisallowedReceive(context, intent);
            return;
        }
        onAllowedReceive(context, intent);
    }

    public abstract void onAllowedReceive(Context context, Intent intent);

    public void onDisallowedReceive(Context context, Intent intent) {
    }
}

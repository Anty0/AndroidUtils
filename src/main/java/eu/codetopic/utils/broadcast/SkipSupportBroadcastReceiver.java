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

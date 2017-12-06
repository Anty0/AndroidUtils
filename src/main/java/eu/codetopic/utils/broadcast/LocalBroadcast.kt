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

package eu.codetopic.utils.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager

/**
 * @author anty
 */
object LocalBroadcast {

    private lateinit var manager: LocalBroadcastManager

    fun initialize(context: Context) {
        manager = LocalBroadcastManager.getInstance(context.applicationContext)
    }

    fun registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
        manager.registerReceiver(receiver, filter)
    }

    fun unregisterReceiver(receiver: BroadcastReceiver) {
        manager.unregisterReceiver(receiver)
    }

    fun sendBroadcast(intent: Intent) {
        manager.sendBroadcast(intent)
    }

    fun sendBroadcastSync(intent: Intent) {
        manager.sendBroadcastSync(intent)
    }
}
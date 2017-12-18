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

package eu.codetopic.utils.notifications.manager.util

import android.app.NotificationChannel
import android.content.Context
import android.os.Build

import eu.codetopic.utils.AndroidExtensions.notificationManager
import eu.codetopic.java.utils.debug.DebugAsserts.assert

/**
 * @author anty
 */
abstract class NotificationChannel(val id: String) {

    fun initialize(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        context.notificationManager.createNotificationChannel(
                createChannel(context).assert { it.id == id }
        )
    }

    protected abstract fun createChannel(context: Context): NotificationChannel
}
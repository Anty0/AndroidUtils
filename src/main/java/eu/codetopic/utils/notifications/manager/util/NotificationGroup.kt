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

import android.app.NotificationChannelGroup
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import eu.codetopic.utils.AndroidExtensions.notificationManager
import eu.codetopic.java.utils.debug.DebugAsserts.assert
import eu.codetopic.utils.notifications.manager.data.NotificationId

/**
 * @author anty
 */
abstract class NotificationGroup(val id: String, val checkForIdOverrides: Boolean) {

    fun initialize(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        context.notificationManager.createNotificationChannelGroup(
                createGroup(context).assert { it.id == id }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    protected abstract fun createGroup(context: Context): NotificationChannelGroup

    abstract fun nextId(context: Context, channel: NotificationChannel, data: Bundle): Int

    abstract fun createNotification(context: Context,
                                    id: NotificationId,
                                    channel: NotificationChannel,
                                    data: Bundle): NotificationCompat.Builder

    open fun handleContentIntent(context: Context, id: NotificationId,
                                 channel: NotificationChannel, data: Bundle) {}

    open fun handleDeleteIntent(context: Context, id: NotificationId,
                                channel: NotificationChannel, data: Bundle) {}
}
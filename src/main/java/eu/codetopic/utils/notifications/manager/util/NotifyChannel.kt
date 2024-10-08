/*
 * utils
 * Copyright (C)   2018  anty
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
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import eu.codetopic.utils.notifications.manager.NotifyClassifier
import eu.codetopic.utils.notifications.manager.data.NotifyId

/**
 * @author anty
 */
abstract class NotifyChannel(val id: String, val checkForIdOverrides: Boolean,
                             val defaultEnabled: Boolean = true) {

    companion object {

        fun combinedId(groupId: String, channelId: String): String =
                "CombinedId(groupId=$groupId, channelId=$channelId)"

        fun combinedIds(channelId: String): List<String> =
                NotifyClassifier.findAllGroupsFor(channelId)
                        .map { combinedId(it.id, channelId) }

        fun combinedIdsMap(channelId: String): Map<String, String> =
                NotifyClassifier.findAllGroupsFor(channelId)
                        .map { it.id.let { it to combinedId(it, channelId) } }
                        .toMap()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    abstract fun createChannel(context: Context, combinedId: String): NotificationChannel

    abstract fun nextId(context: Context, group: NotifyGroup, data: Bundle): Int

    abstract fun createNotification(context: Context, group: NotifyGroup,
                                    notifyId: NotifyId, data: Bundle): NotificationCompat.Builder

    open fun handleContentIntent(context: Context, group: NotifyGroup,
                                 notifyId: NotifyId, data: Bundle) {}

    open fun handleDeleteIntent(context: Context, group: NotifyGroup,
                                notifyId: NotifyId, data: Bundle) {}

    open fun handleCancel(context: Context, group: NotifyGroup?,
                          notifyId: NotifyId, data: Bundle?) {}
}
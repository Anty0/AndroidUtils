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

package eu.codetopic.utils.notifications.manager2.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat

import eu.codetopic.utils.notifications.manager2.NotifyClassifier
import eu.codetopic.utils.notifications.manager2.data.NotifyId

/**
 * @author anty
 */
abstract class NotifyChannel(val id: String, val checkForIdOverrides: Boolean) {

    companion object {

        fun combinedId(groupId: String, channelId: String): String =
                "CombinedId{groupId=$groupId, channelId=$channelId}"

        fun combinedIds(channelId: String): List<String> =
                NotifyClassifier.findAllGroupsFor(channelId)
                        .map { combinedId(it.id, channelId) }

        fun NotifyChannel.combinedIdFor(group: NotifyGroup): String =
                combinedId(group.id, this.id)

        fun NotifyChannel.combinedIds(): List<String> = combinedIds(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    abstract fun createChannel(context: Context, combinedId: String):
            android.app.NotificationChannel

    abstract fun nextId(context: Context, group: NotifyGroup, data: Bundle): Int

    abstract fun createNotification(context: Context, group: NotifyGroup,
                                    notifyId: NotifyId, data: Bundle): NotificationCompat.Builder

    open fun handleContentIntent(context: Context, group: NotifyGroup,
                                 notifyId: NotifyId, data: Bundle) {}

    open fun handleDeleteIntent(context: Context, group: NotifyGroup,
                                notifyId: NotifyId, data: Bundle) {}
}
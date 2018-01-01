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

package eu.codetopic.utils.notifications.manager

import android.content.Context
import android.os.Bundle
import eu.codetopic.utils.notifications.manager.data.NotificationId
import eu.codetopic.utils.notifications.manager.receiver.*
import eu.codetopic.utils.notifications.manager.save.NotificationsData
import eu.codetopic.utils.notifications.manager.util.NotificationChannel
import eu.codetopic.utils.notifications.manager.util.NotificationGroup

/**
 * @author anty
 */
object NotificationsManager {

    val isInitialized: Boolean
        get() = NotificationsData.isInitialized()

    fun assertInitialized() {
        if (!isInitialized) throw IllegalStateException("NotificationsManager is not initialized")
    }

    fun initialize(context: Context) {
        NotificationsData.initialize(context)
        cleanup(context)
        refresh(context)
    }

    //--------------------------------------------------------------------------

    fun initGroup(context: Context, group: NotificationGroup) =
            NotificationsGroups.add(context, group)

    fun initChannel(context: Context, channel: NotificationChannel) =
            NotificationsChannels.add(context, channel)

    fun removeGroup(context: Context, groupId: String): NotificationGroup =
            NotificationsGroups.remove(groupId).also {
                if (isInitialized) cleanup(context)
            }

    fun removeChannel(context: Context, channelId: String): NotificationChannel =
            NotificationsChannels.remove(channelId).also {
                if (isInitialized) cleanup(context)
            }

    fun refreshGroup(context: Context, groupId: String) =
            NotificationsGroups.refresh(context, groupId)

    fun refreshChannel(context: Context, channelId: String) =
            NotificationsChannels.refresh(context, channelId)

    fun initGroups(context: Context, vararg groups: NotificationGroup) =
            groups.forEach { NotificationsGroups.add(context, it) }

    fun initChannels(context: Context, vararg channels: NotificationChannel) =
            channels.forEach { NotificationsChannels.add(context, it) }

    fun refreshGroups(context: Context, vararg groupIds: String) =
            groupIds.forEach { NotificationsGroups.refresh(context, it) }

    fun refreshChannels(context: Context, vararg channelIds: String) =
            channelIds.forEach { NotificationsChannels.refresh(context, it) }

    fun getGroup(groupId: String): NotificationGroup =
            NotificationsGroups[groupId]

    fun getChannel(channelId: String): NotificationChannel =
            NotificationsChannels[channelId]

    fun existsGroup(groupId: String): Boolean = groupId in NotificationsGroups

    fun existsChannel(channelId: String): Boolean = channelId in NotificationsChannels

    //--------------------------------------------------------------------------

    fun refresh(context: Context) = Notifications.refresh(context)

    fun notify(context: Context, groupId: String, channelId: String, data: Bundle): NotificationId =
            Notifications.notify(context, groupId, channelId, data)

    fun notifyAll(context: Context, groupId: String,
                  channelId: String, vararg data: Bundle): List<NotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data.asList())

    fun notifyAll(context: Context, groupId: String,
                  channelId: String, data: List<Bundle>): List<NotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data)

    fun cancel(context: Context, id: NotificationId) = Notifications.cancel(context, id)

    fun cancelAll(context: Context, vararg ids: NotificationId) =
            Notifications.cancelAll(context, ids.asList())

    fun cancelAll(context: Context, ids: List<NotificationId>) =
            Notifications.cancelAll(context, ids)

    fun cancelAll(context: Context, groupId: String? = null, channelId: String? = null) =
            Notifications.cancelAll(context, groupId, channelId)

    fun get(id: NotificationId): Bundle =
            NotificationsData.instance[id]
                    ?: throw IllegalArgumentException("Id doesn't exists: $id")

    fun getAll(groupId: String?, channelId: String? = null) =
            NotificationsData.instance.getAll(groupId, channelId)

    fun cleanup(context: Context) = Notifications.cleanup(context)

    //--------------------------------------------------------------------------

    fun requestRefresh(context: Context) =
            context.sendBroadcast(RequestRefreshReceiver.getStartIntent(context))

    fun requestNotify(context: Context, groupId: String, channelId: String, data: Bundle) =
            context.sendBroadcast(RequestNotifyReceiver
                    .getStartIntent(context, groupId, channelId, data))

    fun requestNotifyAll(context: Context, groupId: String,
                         channelId: String, vararg data: Bundle) =
            requestNotifyAll(context, groupId, channelId, data.asList())

    fun requestNotifyAll(context: Context, groupId: String,
                         channelId: String, data: List<Bundle>) =
            context.sendBroadcast(RequestNotifyAllReceiver
                    .getStartIntent(context, groupId, channelId, data))

    fun requestCancel(context: Context, id: NotificationId) =
            context.sendBroadcast(RequestCancelReceiver.getStartIntent(context, id))

    fun requestCancelAll(context: Context, vararg ids: NotificationId) =
            requestCancelAll(context, ids.asList())

    fun requestCancelAll(context: Context, ids: List<NotificationId>) =
            context.sendBroadcast(RequestCancelAllIdsReceiver.getStartIntent(context, ids))

    fun requestCancelAll(context: Context, groupId: String? = null, channelId: String? = null) =
            context.sendBroadcast(RequestCancelAllReceiver
                    .getStartIntent(context, groupId, channelId))
}
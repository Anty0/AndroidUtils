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

    fun notify(context: Context, groupId: String, channelId: String, data: Bundle,
               whenTime: Long = System.currentTimeMillis()): NotificationId =
            Notifications.notify(context, groupId, channelId, data, whenTime)

    fun notifyAll(context: Context, groupId: String, channelId: String, vararg data: Bundle,
                  whenTime: Long = System.currentTimeMillis()): List<NotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data.asList(), whenTime)

    fun notifyAll(context: Context, groupId: String, channelId: String, data: List<Bundle>,
                  whenTime: Long = System.currentTimeMillis()): List<NotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data, whenTime)

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

    fun requestRefresh(context: Context, optimise: Boolean = true) {
        if (optimise && isInitialized) refresh(context)
        else context.sendBroadcast(RequestRefreshReceiver.getStartIntent(context))
    }

    fun requestNotify(context: Context, groupId: String, channelId: String, data: Bundle,
                      whenTime: Long = System.currentTimeMillis(), optimise: Boolean = true) {
        if (optimise && isInitialized) notify(context, groupId, channelId, data, whenTime)
        else context.sendBroadcast(
                RequestNotifyReceiver.getStartIntent(context, groupId, channelId, data, whenTime)
        )
    }

    fun requestNotifyAll(context: Context, groupId: String, channelId: String, vararg data: Bundle,
                         whenTime: Long = System.currentTimeMillis(), optimise: Boolean = true) =
            requestNotifyAll(context, groupId, channelId, data.asList(), whenTime, optimise)

    fun requestNotifyAll(context: Context, groupId: String, channelId: String, data: List<Bundle>,
                         whenTime: Long = System.currentTimeMillis(), optimise: Boolean = true) {
        if (optimise && isInitialized) notifyAll(context, groupId, channelId, data, whenTime)
        else context.sendBroadcast(
                RequestNotifyAllReceiver.getStartIntent(context, groupId, channelId, data, whenTime)
        )
    }

    fun requestCancel(context: Context, id: NotificationId, optimise: Boolean = true) {
        if (optimise && isInitialized) cancel(context, id)
        else context.sendBroadcast(RequestCancelReceiver.getStartIntent(context, id))
    }

    fun requestCancelAll(context: Context, vararg ids: NotificationId, optimise: Boolean = true) =
            requestCancelAll(context, ids.asList(), optimise)

    fun requestCancelAll(context: Context, ids: List<NotificationId>, optimise: Boolean = true) {
        if (optimise && isInitialized) cancelAll(context, ids)
        else context.sendBroadcast(RequestCancelAllIdsReceiver.getStartIntent(context, ids))
    }

    fun requestCancelAll(context: Context, groupId: String? = null,
                         channelId: String? = null, optimise: Boolean = true) {
        if (optimise && isInitialized) cancelAll(context, groupId, channelId)
        else context.sendBroadcast(
                RequestCancelAllReceiver.getStartIntent(context, groupId, channelId)
        )
    }
}
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

import android.annotation.SuppressLint
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
@SuppressLint("StaticFieldLeak")
object NotificationsManager {

    private lateinit var context: Context

    val isInitialized: Boolean = ::context.isInitialized

    fun assertInitialized() {
        if (!isInitialized) throw IllegalStateException("NotificationsManager is not initialized")
    }

    fun initialize(context: Context) {
        NotificationsData.initialize(context)
        this.context = context.applicationContext
    }

    //--------------------------------------------------------------------------

    fun initGroup(group: NotificationGroup) =
            NotificationsGroups.add(context, group)

    fun initChannel(channel: NotificationChannel) =
            NotificationsChannels.add(context, channel)

    fun refreshGroup(groupId: String) =
            NotificationsGroups.refresh(context, groupId)

    fun refreshChannel(channelId: String) =
            NotificationsChannels.refresh(context, channelId)

    //--------------------------------------------------------------------------

    fun refresh() = Notifications.refresh(context)

    fun notify(groupId: String, channelId: String, data: Bundle): NotificationId =
            Notifications.notify(context, groupId, channelId, data)

    fun notifyAll(groupId: String, channelId: String, vararg data: Bundle): List<NotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data.asList())

    fun notifyAll(groupId: String, channelId: String, data: List<Bundle>): List<NotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data)

    fun cancel(id: NotificationId) = Notifications.cancel(context, id)

    fun cancelAll(vararg ids: NotificationId) =
            Notifications.cancelAll(context, ids.asList())

    fun cancelAll(ids: List<NotificationId>) =
            Notifications.cancelAll(context, ids)

    fun cancelAll(groupId: String? = null, channelId: String? = null) =
            Notifications.cancelAll(context, groupId, channelId)

    fun get(id: NotificationId): Bundle =
            NotificationsData.instance[id]
                    ?: throw IllegalArgumentException("Id doesn't exists: $id")

    fun getAll(groupId: String?, channelId: String?) =
            NotificationsData.instance.getAll(groupId, channelId)

    //--------------------------------------------------------------------------

    fun requestRefresh() =
            context.sendBroadcast(RequestRefreshReceiver.getStartIntent(context))

    fun requestNotify(groupId: String, channelId: String, data: Bundle) =
            context.sendBroadcast(RequestNotifyReceiver
                    .getStartIntent(context, groupId, channelId, data))

    fun requestNotifyAll(groupId: String, channelId: String, vararg data: Bundle) =
            requestNotifyAll(groupId, channelId, data.asList())

    fun requestNotifyAll(groupId: String, channelId: String, data: List<Bundle>) =
            context.sendBroadcast(RequestNotifyAllReceiver
                    .getStartIntent(context, groupId, channelId, data))

    fun requestCancel(id: NotificationId) =
            context.sendBroadcast(RequestCancelReceiver.getStartIntent(context, id))

    fun requestCancelAll(vararg ids: NotificationId) =
            requestCancelAll(ids.asList())

    fun requestCancelAll(ids: List<NotificationId>) =
            context.sendBroadcast(RequestCancelAllIdsReceiver.getStartIntent(context, ids))

    fun requestCancelAll(groupId: String? = null, channelId: String? = null) =
            context.sendBroadcast(RequestCancelAllReceiver
                    .getStartIntent(context, groupId, channelId))
}
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
import eu.codetopic.utils.notifications.manager.data.CommonNotificationId
import eu.codetopic.utils.notifications.manager.save.NotificationsData
import eu.codetopic.utils.notifications.manager.util.NotificationChannel
import eu.codetopic.utils.notifications.manager.util.NotificationGroup

/**
 * @author anty
 */
@SuppressLint("StaticFieldLeak")
object NotificationsManager {


    private lateinit var context: Context

    fun initialize(context: Context) {
        NotificationsData.initialize(context)
        this.context = context.applicationContext
    }

    //--------------------------------------------------------------------------

    fun initGroup(group: NotificationGroup) =
            NotificationsGroups.add(context, group)

    fun initChannel(channel: NotificationChannel) =
            NotificationsChannels.add(context, channel)

    //--------------------------------------------------------------------------

    fun refresh() = Notifications.refresh(context)

    fun notify(groupId: String, channelId: String, data: Bundle): CommonNotificationId =
            Notifications.notify(context, groupId, channelId, data)

    fun notifyAll(groupId: String, channelId: String, vararg data: Bundle): List<CommonNotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data.asList())

    fun notifyAll(groupId: String, channelId: String, data: List<Bundle>): List<CommonNotificationId> =
            Notifications.notifyAll(context, groupId, channelId, data)

    fun cancel(id: CommonNotificationId) = Notifications.cancel(context, id)

    fun cancelAll(vararg ids: CommonNotificationId) =
            Notifications.cancelAll(context, ids.asList())

    fun cancelAll(ids: List<CommonNotificationId>) =
            Notifications.cancelAll(context, ids)

    fun cancelAll(groupId: String? = null, channelId: String? = null) =
            Notifications.cancelAll(context, groupId, channelId)

    // TODO: fun get(id)

    // TODO: fun getAll(groupId?, channelId?)

    //--------------------------------------------------------------------------

    fun requestRefresh() {
        TODO("Not implemented")
    }

    fun requestNotify(groupId: String, channelId: String, data: Bundle) {
        TODO("Not implemented")
    }

    fun requestNotifyAll(groupId: String, channelId: String, vararg data: Bundle) {
        TODO("Not implemented")
    }

    fun requestCancel(id: CommonNotificationId) {
        TODO("Not implemented")
    }

    fun requestCancelAll(vararg ids: CommonNotificationId) {
        TODO("Not implemented")
    }

    fun requestCancelAll(groupId: String? = null, channelId: String? = null) {
        TODO("Not implemented")
    }
}
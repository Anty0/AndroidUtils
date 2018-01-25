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

package eu.codetopic.utils.notifications.manager2

import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.utils.notifications.manager2.util.NotifyChannel
import eu.codetopic.utils.notifications.manager2.util.NotifyGroup
import eu.codetopic.java.utils.JavaExtensions.alsoIf
import eu.codetopic.utils.notifications.manager2.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager2.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager2.data.NotifyId
import eu.codetopic.utils.notifications.manager2.receiver.*
import eu.codetopic.utils.notifications.manager2.save.NotifyData

/**
 * @author anty
 */
object NotifyManager {

    val isInitialized: Boolean
        get() = NotifyData.isInitialized()

    fun assertInitialized() {
        if (!isInitialized) throw IllegalStateException("NotifyManager is not initialized")
    }

    @MainThread
    fun completeInitialization(context: Context) {
        NotifyData.initialize(context)
        cleanup(context)
        refresh(context)
    }

    //--------------------------------------------------------------------------

    @MainThread
    fun installGroup(context: Context, group: NotifyGroup) =
            NotifyClassifier.install(context, group)

    @MainThread
    fun installChannel(context: Context, channel: NotifyChannel) =
            NotifyClassifier.install(context, channel)

    @MainThread
    fun uninstallGroup(context: Context, groupId: String): NotifyGroup =
            NotifyClassifier.uninstallGroup(context, groupId)
                    .alsoIf({ isInitialized }) { cleanup(context) }

    @MainThread
    fun uninstallChannel(context: Context, channelId: String): NotifyChannel =
            NotifyClassifier.uninstallChannel(context, channelId)
                    .alsoIf({ isInitialized }) { cleanup(context) }

    @MainThread
    fun reinstallGroup(context: Context, groupId: String) =
            NotifyClassifier.reinstallGroup(context, groupId)
                    .alsoIf({ isInitialized }) { refresh(context) }

    @MainThread
    fun reinstallChannel(context: Context, channelId: String) =
            NotifyClassifier.reinstallChannel(context, channelId)
                    .alsoIf({ isInitialized }) { refresh(context) }

    @MainThread
    fun installGroups(context: Context, vararg groups: NotifyGroup) =
            groups.forEach { NotifyClassifier.install(context, it) }

    @MainThread
    fun initChannels(context: Context, vararg channels: NotifyChannel) =
            channels.forEach { NotifyClassifier.install(context, it) }

    @MainThread
    fun reinstallGroups(context: Context, vararg groupIds: String) =
            groupIds.forEach { NotifyClassifier.reinstallGroup(context, it) }
                    .alsoIf({ isInitialized }) { refresh(context) }

    @MainThread
    fun reinstallChannels(context: Context, vararg channelIds: String) =
            channelIds.forEach { NotifyClassifier.reinstallChannel(context, it) }
                    .alsoIf({ isInitialized }) { refresh(context) }

    fun findGroup(groupId: String): NotifyGroup =
            NotifyClassifier.findGroup(groupId)

    fun findChannel(channelId: String): NotifyChannel =
            NotifyClassifier.findChannel(channelId)

    fun hasGroup(groupId: String): Boolean =
            NotifyClassifier.hasGroup(groupId)

    fun hasChannel(channelId: String): Boolean =
            NotifyClassifier.hasChannel(channelId)

    //--------------------------------------------------------------------------

    @MainThread
    fun refresh(context: Context) = Notifier.refresh(context)

    @MainThread
    fun cleanup(context: Context) = Notifier.cleanup(context)

    @MainThread
    fun notify(context: Context, builder: NotificationBuilder): NotifyId =
            Notifier.notify(context, builder)

    @MainThread
    fun notifyAll(context: Context, builder: MultiNotificationBuilder): Map<NotifyId, Bundle> =
            Notifier.notifyAll(context, builder)

    @MainThread
    fun cancel(context: Context, notifyId: NotifyId): Bundle? =
            Notifier.cancel(context, notifyId)

    @MainThread
    fun cancelAll(context: Context, vararg notifyIds: NotifyId): Map<NotifyId, Bundle> =
            Notifier.cancelAll(context, notifyIds.asList())

    @MainThread
    fun cancelAll(context: Context, notifyIds: Collection<NotifyId>): Map<NotifyId, Bundle> =
            Notifier.cancelAll(context, notifyIds)

    @MainThread
    fun cancelAll(context: Context, groupId: String? = null, channelId: String? = null) =
            Notifier.cancelAll(context, groupId, channelId)

    @MainThread
    fun getDataOf(notifyId: NotifyId): Bundle =
            NotifyData.instance[notifyId]
                    ?: throw IllegalArgumentException("Id doesn't exists: $notifyId")

    @MainThread
    fun getAllData(groupId: String? = null, channelId: String? = null) =
            NotifyData.instance.getAll(groupId, channelId)

    //--------------------------------------------------------------------------

    @MainThread
    fun requestRefresh(context: Context, optimise: Boolean = true) {
        if (optimise && isInitialized) refresh(context)
        else context.sendBroadcast(RqRefreshReceiver.getStartIntent(context))
    }

    @MainThread
    fun requestNotify(context: Context, builder: NotificationBuilder, optimise: Boolean = true) {
        if (optimise && isInitialized) notify(context, builder)
        else context.sendBroadcast(
                RqNotifyReceiver.getStartIntent(context, builder)
        )
    }

    @MainThread
    fun requestNotifyAll(context: Context, builder: MultiNotificationBuilder,
                         optimise: Boolean = true) {
        if (optimise && isInitialized) notifyAll(context, builder)
        else context.sendBroadcast(
                RqNotifyAllReceiver.getStartIntent(context, builder)
        )
    }

    @MainThread
    fun requestCancel(context: Context, notifyId: NotifyId, optimise: Boolean = true) {
        if (optimise && isInitialized) cancel(context, notifyId)
        else context.sendBroadcast(RqCancelReceiver.getStartIntent(context, notifyId))
    }

    @MainThread
    fun requestCancelAll(context: Context, vararg notifyIds: NotifyId, optimise: Boolean = true) =
            requestCancelAll(context, notifyIds.asList(), optimise)

    @MainThread
    fun requestCancelAll(context: Context, notifyIds: Collection<NotifyId>, optimise: Boolean = true) {
        if (optimise && isInitialized) cancelAll(context, notifyIds)
        else context.sendBroadcast(RqCancelAllIdsReceiver.getStartIntent(context, notifyIds))
    }

    @MainThread
    fun requestCancelAll(context: Context, groupId: String? = null,
                         channelId: String? = null, optimise: Boolean = true) {
        if (optimise && isInitialized) cancelAll(context, groupId, channelId)
        else context.sendBroadcast(
                RqCancelAllReceiver.getStartIntent(context, groupId, channelId)
        )
    }
}
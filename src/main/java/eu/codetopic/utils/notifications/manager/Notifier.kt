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

package eu.codetopic.utils.notifications.manager

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.ids.Identifiers.Companion.nextId
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder.Companion.build
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder.Companion.build
import eu.codetopic.utils.notifications.manager.data.CommonNotifyId
import eu.codetopic.utils.notifications.manager.data.CommonPersistentNotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyId.Companion.idCombined
import eu.codetopic.utils.notifications.manager.data.NotifyId.Companion.group
import eu.codetopic.utils.notifications.manager.data.NotifyId.Companion.channel
import eu.codetopic.utils.notifications.manager.data.NotifyId.Companion.tag
import eu.codetopic.utils.notifications.manager.data.SummaryNotifyId
import eu.codetopic.utils.notifications.manager.receiver.internal.*
import eu.codetopic.utils.notifications.manager.save.NotifyData
import eu.codetopic.utils.notifications.manager.util.SummarizedNotifyChannel

/**
 * @author anty
 */
@MainThread
internal object Notifier {

    private const val LOG_TAG = "Notifier"

    private fun NotifyId.launchIntent(context: Context, data: Bundle): PendingIntent =
            when (this) {
                is CommonNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonNotifyLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonNotifyLaunchReceiver.getIntent(context, this, data),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
                is CommonPersistentNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonPersistentNotifyLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonPersistentNotifyLaunchReceiver.getIntent(context, this),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
                is SummaryNotifyId -> PendingIntent.getBroadcast(
                        context,
                        SummaryNotifyLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                        SummaryNotifyLaunchReceiver.getIntent(context, this),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
                else -> throw IllegalArgumentException("Unknown notifyId: ${this::class}")
            }

    private fun NotifyId.deleteIntent(context: Context, data: Bundle): PendingIntent =
            when (this) {
                is CommonNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonNotifyDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonNotifyDeleteReceiver.getIntent(context, this, data),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                )
                is CommonPersistentNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonPersistentNotifyDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonPersistentNotifyDeleteReceiver.getIntent(context, this),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                )
                is SummaryNotifyId -> PendingIntent.getBroadcast(
                        context,
                        SummaryNotifyDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                        SummaryNotifyDeleteReceiver.getIntent(context, this),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                )
                else -> throw IllegalArgumentException("Unknown notifyId: ${this::class}")
            }

    private fun NotificationCompat.Builder.normalize(context: Context,
                                                     notifyId: NotifyId, data: Bundle,
                                                     isRefresh: Boolean): NotificationCompat.Builder {
        val combinedId = notifyId.idCombined
        return this.apply {
            setChannelId(combinedId)
            setGroup(combinedId)
            setGroupSummary(notifyId.isSummary)
            setOnlyAlertOnce(isRefresh)
            setWhen(notifyId.timeWhen)
            setContentIntent(notifyId.launchIntent(context, data))
            setDeleteIntent(notifyId.deleteIntent(context, data))
        }
    }

    private fun NotifyId.buildNotification(context: Context, data: Bundle, isRefresh: Boolean): Notification {
        return channel.createNotification(context, group, this, data)
                .normalize(context, this, data, isRefresh)
                .build()
    }

    private fun NotifyId.buildSummaryNotification(context: Context,
                                                  data: Map<NotifyId, Bundle>): Notification {
        val channel = channel as? SummarizedNotifyChannel
                ?: throw IllegalArgumentException("Can't build summary notification:" +
                        " Channel is not SummarizedNotifyChannel")

        return channel.createSummaryNotification(context, group, this, data)
                .normalize(context, this, Bundle.EMPTY, true)
                .build()
    }

    private fun Notification.show(context: Context, notifyId: NotifyId) =
            show(NotificationManagerCompat.from(context), notifyId)

    private fun Notification.show(notifier: NotificationManagerCompat, notifyId: NotifyId) {
        try {
            notifier.notify(notifyId.tag, notifyId.idNotify, this)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Notification.show(notifyId=$notifyId)" +
                    " -> Failed to show notification", e)
        }
    }

    private fun NotifyId.showNotification(context: Context, notifier: NotificationManagerCompat,
                                          data: Bundle, isRefresh: Boolean) =
            try {
                buildNotification(context, data, isRefresh).show(notifier, this)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "NotifyId.showNotification(this=$this, data=$data)" +
                        " -> failed to build notification", e)
            }

    private fun NotifyId.showNotification(context: Context, data: Bundle, isRefresh: Boolean) =
            try {
                buildNotification(context, data, isRefresh).show(context, this)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "NotifyId.showNotification(this=$this, data=$data)" +
                        " -> failed to build notification", e)
            }

    private fun NotifyId.cancelNotification(context: Context) =
            cancelNotification(NotificationManagerCompat.from(context))

    private fun NotifyId.cancelNotification(notifier: NotificationManagerCompat) {
        try {
            notifier.cancel(tag, idNotify)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "NotifyId.cancel(this=$this)" +
                    " -> Failed to cancel notification", e)
        }
    }

    private fun SummaryNotifyId.showSummaryNotification(context: Context,
                                                 notifier: NotificationManagerCompat,
                                                 data: Map<NotifyId, Bundle>) =
            try {
                buildSummaryNotification(context, data).show(notifier, this)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "NotifyId.showSummaryNotification(this=$this, data=$data)" +
                        " -> failed to build notification", e)
            }

    private fun SummaryNotifyId.showSummaryNotification(context: Context,
                                                        data: Map<NotifyId, Bundle>) =
            try {
                buildSummaryNotification(context, data).show(context, this)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "NotifyId.showSummaryNotification(this=$this, data=$data)" +
                        " -> failed to build notification", e)
            }

    private fun refreshSummaries(context: Context) {
        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = NotifyData.instance.getAll()

        NotifyClassifier.findAllGroups().forEach forGroups@ { group ->
            val groupId = group.id
            val groupNotifyMap = notifyMap.filter { it.key.idGroup == groupId }
            group.channelIds.forEach forChannels@ { channelId ->
                val channel = NotifyClassifier.findChannel(channelId) as? SummarizedNotifyChannel
                        ?: return@forChannels
                val channelNotifyMap = groupNotifyMap
                        .filter { it.key.idChannel == channelId }
                        .takeIf { it.isNotEmpty() }
                val summaryNotifyId = SummaryNotifyId(
                        idGroup = groupId,
                        idChannel = channelId,
                        timeWhen = channelNotifyMap?.keys?.maxBy { it.timeWhen }?.timeWhen
                                ?: System.currentTimeMillis()
                )

                if (channelNotifyMap != null)
                    summaryNotifyId.showSummaryNotification(context, notifier, channelNotifyMap)
                else summaryNotifyId.cancelNotification(notifier)
            }
        }
    }

    fun refreshSummaryOf(context: Context, notifyId: NotifyId) =
            refreshSummaryOf(context, notifyId.idGroup, notifyId.idChannel)

    private fun refreshSummaryOf(context: Context, builder: MultiNotificationBuilder) =
            refreshSummaryOf(context, builder.groupId, builder.channelId)

    private fun refreshSummaryOf(context: Context, groupId: String, channelId: String) {
        val notifyMap = NotifyData.instance.getAll(groupId, channelId)
                .takeIf { it.isNotEmpty() }
        val summaryNotifyId = SummaryNotifyId(
                idGroup = groupId,
                idChannel = channelId,
                timeWhen = notifyMap?.keys?.maxBy { it.timeWhen }?.timeWhen
                        ?: System.currentTimeMillis()
        )

        if (notifyMap != null)
            summaryNotifyId.showSummaryNotification(context, notifyMap)
        else summaryNotifyId.cancelNotification(context)
    }

    fun refresh(context: Context) {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)

        NotifyData.instance.getAll().forEach {
            val (notifyId, data) = it
            notifyId.takeIf { it.isRefreshable }
                    ?.showNotification(context, notifier, data, true)
        }

        refreshSummaries(context)
    }

    fun bootCleanup(context: Context) {
        NotifyManager.assertInitialized(context)

        // Remove all non refreshable notifyIds,
        //  because they won't be visible again
        NotifyData.instance.removeAll(
                NotifyData.instance.getAll().keys
                        .filter { !it.isRefreshable }
        )
    }

    fun cleanup(context: Context): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        return cancelAll(
                context,
                NotifyData.instance.getAll().keys.filter {
                    !NotifyClassifier.hasGroup(it.idGroup) ||
                            !NotifyClassifier.hasChannel(it.idChannel)
                }
        )
    }

    fun notify(context: Context, builder: NotificationBuilder): NotifyId {
        NotifyManager.assertInitialized(context)

        val (notifyId, data) = builder.build(context)

        NotifyData.instance.add(notifyId, data)

        notifyId.showNotification(context, data, false)
        refreshSummaryOf(context, notifyId)

        return notifyId
    }

    fun notifyAll(context: Context, builder: MultiNotificationBuilder): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = builder.build(context)

        NotifyData.instance.addAll(notifyMap)

        notifyMap.forEach {
            val (notifyId, data) = it
            notifyId.showNotification(context, notifier, data, false)
        }

        refreshSummaryOf(context, builder)

        return notifyMap
    }

    fun cancel(context: Context, notifyId: NotifyId): Bundle? {
        NotifyManager.assertInitialized(context)

        val data = NotifyData.instance.remove(notifyId)
        if (DebugMode.isEnabled && notifyId.isPersistent && data == null) {
            Log.e(LOG_TAG, "cancel(notifyId=$notifyId)",
                    IllegalStateException("Notification doesn't exists in NotifyData"))
        }

        notifyId.cancelNotification(context)

        refreshSummaryOf(context, notifyId)

        return data
    }

    fun cancelAll(context: Context, notifyIds: Collection<NotifyId>): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = NotifyData.instance.removeAll(notifyIds)

        notifyIds.forEach {
            it.cancelNotification(notifier)
        }

        refreshSummaries(context)

        return notifyMap
    }

    fun cancelAll(context: Context, groupId: String? = null,
                  channelId: String? = null): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = NotifyData.instance.removeAll(groupId, channelId)

        notifyMap.keys.forEach {
            it.cancelNotification(notifier)
        }

        if (groupId != null && channelId != null)
            refreshSummaryOf(context, groupId, channelId)
        else refreshSummaries(context)

        return notifyMap
    }
}
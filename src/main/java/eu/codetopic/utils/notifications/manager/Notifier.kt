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
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.ids.Identifiers.Companion.nextId
import eu.codetopic.utils.notifications.manager.create.MultiNotificationBuilder
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.data.*
import eu.codetopic.utils.notifications.manager.receiver.internal.*
import eu.codetopic.utils.notifications.manager.save.NotifyData
import eu.codetopic.utils.notifications.manager.util.SummarizedNotifyChannel

/**
 * @author anty
 */
@MainThread
internal object Notifier {

    private const val LOG_TAG = "Notifier"

    private fun NotificationBuilder.build(context: Context, hasTag: Boolean): Pair<NotifyId, Bundle> {
        val (groupId, channelId, timeWhen, persistent, refreshable, data) = this
        val group = NotifyClassifier.findGroup(groupId)
        val channel = NotifyClassifier.findChannel(channelId)
        val notifyId = channel.nextId(context, group, data)

        val id = if (persistent && hasTag)
            CommonPersistentNotifyId(groupId, channelId, notifyId, timeWhen, refreshable)
        else CommonNotifyId(groupId, channelId, notifyId, hasTag, timeWhen)
        return id to data
    }

    private fun MultiNotificationBuilder.build(context: Context, hasTag: Boolean): Map<NotifyId, Bundle> {
        val (groupId, channelId, timeWhen, persistent, refreshable, data) = this
        val group = NotifyClassifier.findGroup(groupId)
        val channel = NotifyClassifier.findChannel(channelId)

        return data.map {
            val notifyId = channel.nextId(context, group, it)

            val id = if (persistent && hasTag)
                CommonPersistentNotifyId(groupId, channelId, notifyId, timeWhen, refreshable)
            else CommonNotifyId(groupId, channelId, notifyId, hasTag, timeWhen)

            return@map id to it
        }.toMap()
    }

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    private fun NotifyId.launchIntent(context: Context, data: Bundle,
                                      autoCancel: Boolean): PendingIntent =
            when (this) {
                is CommonNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonNotifyLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonNotifyLaunchReceiver
                                .getIntent(context, this, data, autoCancel)
                                .also {
                                    if (Build.VERSION.SDK_INT >= 16)
                                        it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                },
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
                is CommonPersistentNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonPersistentNotifyLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonPersistentNotifyLaunchReceiver
                                .getIntent(context, this, autoCancel)
                                .also {
                                    if (Build.VERSION.SDK_INT >= 16)
                                        it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                },
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
                is SummaryNotifyId -> PendingIntent.getBroadcast(
                        context,
                        SummaryNotifyLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                        SummaryNotifyLaunchReceiver
                                .getIntent(context, this, autoCancel)
                                .also {
                                    if (Build.VERSION.SDK_INT >= 16)
                                        it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                },
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
                else -> throw IllegalArgumentException("Unknown notifyId: ${this::class}")
            }

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    private fun NotifyId.deleteIntent(context: Context, data: Bundle): PendingIntent =
            when (this) {
                is CommonNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonNotifyDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonNotifyDeleteReceiver.getIntent(context, this, data)
                                .also {
                                    if (Build.VERSION.SDK_INT >= 16)
                                        it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                )
                is CommonPersistentNotifyId -> PendingIntent.getBroadcast(
                        context,
                        CommonPersistentNotifyDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                        CommonPersistentNotifyDeleteReceiver.getIntent(context, this)
                                .also {
                                    if (Build.VERSION.SDK_INT >= 16)
                                        it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                )
                is SummaryNotifyId -> PendingIntent.getBroadcast(
                        context,
                        SummaryNotifyDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                        SummaryNotifyDeleteReceiver.getIntent(context, this)
                                .also {
                                    if (Build.VERSION.SDK_INT >= 16)
                                        it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                )
                else -> throw IllegalArgumentException("Unknown notifyId: ${this::class}")
            }

    private fun NotificationCompat.Builder.normalize(context: Context, notifyId: NotifyId,
                                                     data: Bundle): NotificationCompat.Builder {
        val combinedId = notifyId.idCombined
        // If notification is auto-cancel, we must call delete intent manually from launch intent,
        //  because of never resolved bug in android.
        val autoCancel = (build().flags and Notification.FLAG_AUTO_CANCEL) != 0

        return this.apply {
            setChannelId(combinedId)
            setGroup(combinedId)
            setGroupSummary(notifyId.isSummary)
            setOnlyAlertOnce(notifyId.isRefreshable)
            setWhen(notifyId.timeWhen)
            setContentIntent(notifyId.launchIntent(context, data, autoCancel))
            setDeleteIntent(notifyId.deleteIntent(context, data))
        }
    }

    private fun NotifyId.buildNotification(context: Context, data: Bundle): Notification {
        return channel.createNotification(context, group, this, data)
                .normalize(context, this, data)
                .build()
    }

    private fun NotifyId.buildSummaryNotification(context: Context,
                                                  data: Map<out NotifyId, Bundle>): Notification {
        val channel = channel as? SummarizedNotifyChannel
                ?: throw IllegalArgumentException("Can't build summary notification:" +
                        " Channel is not SummarizedNotifyChannel")

        return channel.createSummaryNotification(context, group, this, data)
                .normalize(context, this, Bundle.EMPTY)
                .build()
    }

    private fun Notification.show(context: Context, notifyId: NotifyId) =
            show(NotificationManagerCompat.from(context), notifyId)

    private fun Notification.show(notifier: NotificationManagerCompat, notifyId: NotifyId) {
        try {
            val enabled = NotifyData.instance.isChannelEnabled(notifyId.idGroup, notifyId.idChannel)
                    ?: notifyId.channel.defaultEnabled

            if (enabled) {
                notifier.notify(notifyId.tag, notifyId.idNotify, this)
            } else {
                notifier.cancel(notifyId.tag, notifyId.idNotify)
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Notification.show(notifyId=$notifyId)" +
                    " -> Failed to show notification", e)
        }
    }

    private fun NotifyId.showNotification(context: Context, notifier: NotificationManagerCompat,
                                          data: Bundle) =
            try {
                buildNotification(context, data).show(notifier, this)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "NotifyId.showNotification(this=$this, data=$data)" +
                        " -> failed to build notification", e)
            }

    private fun NotifyId.showNotification(context: Context, data: Bundle) =
            try {
                buildNotification(context, data).show(context, this)
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
                                                 data: Map<out NotifyId, Bundle>) =
            try {
                buildSummaryNotification(context, data).show(notifier, this)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "NotifyId.showSummaryNotification(this=$this, data=$data)" +
                        " -> failed to build notification", e)
            }

    private fun SummaryNotifyId.showSummaryNotification(context: Context,
                                                        data: Map<out NotifyId, Bundle>) =
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
                NotifyClassifier.findChannel(channelId) as? SummarizedNotifyChannel
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
        if (NotifyClassifier.hasChannel(channelId)) {
            NotifyClassifier.findChannel(channelId) as? SummarizedNotifyChannel ?: return

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
        } else {
            val summaryNotifyId = SummaryNotifyId(
                    idGroup = groupId,
                    idChannel = channelId
            )

            summaryNotifyId.cancelNotification(context)
        }
    }

    fun refresh(context: Context) {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)

        NotifyData.instance.getAll()
                .filter { it.key.isRefreshable }
                .forEach {
                    val (notifyId, data) = it
                    notifyId.showNotification(context, notifier, data)
                }

        refreshSummaries(context)
    }

    fun refresh(context: Context, groupId: String?, channelId: String?) {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)

        NotifyData.instance.getAll()
                .filter {
                    it.key.let {
                        it.isRefreshable
                                && it.idGroup == groupId
                                && it.idChannel == channelId
                    }
                }
                .forEach {
                    val (notifyId, data) = it
                    notifyId.showNotification(context, notifier, data)
                }

        refreshSummaries(context)
    }

    fun bootCleanup(context: Context) {
        NotifyManager.assertInitialized(context)

        // Cancel all non refreshable notifyIds,
        //  because they won't be visible again.
        cancelAll(
                context,
                NotifyData.instance.getAll().keys
                        .filter { !it.isRefreshable }
        )
    }

    fun cleanup(context: Context): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        return cancelAll(
                context,
                NotifyData.instance.getAll().keys.filter {
                    !NotifyClassifier.hasGroup(it.idGroup) ||
                            !NotifyClassifier.hasChannel(it.idChannel)
                }
        )
    }


    fun build(context: Context, builder: NotificationBuilder,
              hasTag: Boolean): Pair<NotifyId, Notification> {
        NotifyManager.assertUsable()

        DebugMode.ifEnabled {
            if (builder.persistent) {
                Log.w(LOG_TAG, "build(builder=$builder)" +
                        " -> Notification for raw build can't be persistent." +
                        " Notification will be build as non persistent.")
            }
        }

        builder.apply {
            persistent = false
            refreshable = false
        }

        val (notifyId, data) = builder.build(context, hasTag)

        val notification = notifyId.buildNotification(context, data)

        return notifyId to notification
    }

    fun notify(context: Context, builder: NotificationBuilder): NotifyId {
        NotifyManager.assertInitialized(context)

        val (notifyId, data) = builder.build(context, true)

        NotifyData.instance.add(notifyId, data)

        notifyId.showNotification(context, data)
        refreshSummaryOf(context, notifyId)

        return notifyId
    }

    fun notifyAll(context: Context, builder: MultiNotificationBuilder): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = builder.build(context, true)

        NotifyData.instance.addAll(notifyMap)

        notifyMap.forEach {
            val (notifyId, data) = it
            notifyId.showNotification(context, notifier, data)
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

        val channel = NotifyClassifier
                .takeIf { it.hasChannel(notifyId.idChannel) }
                ?.findChannel(notifyId.idChannel)
        val group = NotifyClassifier
                .takeIf { channel != null && it.hasGroup(notifyId.idGroup) }
                ?.findGroup(notifyId.idGroup)

        notifyId.cancelNotification(context)

        channel?.handleCancel(context, group, notifyId, data)

        refreshSummaryOf(context, notifyId)

        return data
    }

    fun cancelAll(context: Context, notifyIds: Collection<NotifyId>): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = NotifyData.instance.removeAll(notifyIds)

        notifyIds.forEach { notifyId ->
            val channel = NotifyClassifier
                    .takeIf { it.hasChannel(notifyId.idChannel) }
                    ?.findChannel(notifyId.idChannel)
            val group = NotifyClassifier
                    .takeIf { channel != null && it.hasGroup(notifyId.idGroup) }
                    ?.findGroup(notifyId.idGroup)

            notifyId.cancelNotification(notifier)

            channel?.handleCancel(context, group, notifyId, notifyMap[notifyId])
        }

        refreshSummaries(context)

        return notifyMap
    }

    fun cancelAll(context: Context, groupId: String? = null,
                  channelId: String? = null): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        val notifier = NotificationManagerCompat.from(context)
        val notifyMap = NotifyData.instance.removeAll(groupId, channelId)

        if (groupId != null && channelId != null) {
            val channel = NotifyClassifier
                    .takeIf { it.hasChannel(channelId) }
                    ?.findChannel(channelId)
            val group = NotifyClassifier
                    .takeIf { channel != null && it.hasGroup(groupId) }
                    ?.findGroup(groupId)

            notifyMap.forEach {
                val (notifyId, data) = it
                notifyId.cancelNotification(notifier)

                channel?.handleCancel(context, group, notifyId, data)
            }
        } else {
            notifyMap.forEach {
                val (notifyId, data) = it

                val channel = NotifyClassifier
                        .takeIf { it.hasChannel(notifyId.idChannel) }
                        ?.findChannel(notifyId.idChannel)
                val group = NotifyClassifier
                        .takeIf { channel != null && it.hasGroup(notifyId.idGroup) }
                        ?.findGroup(notifyId.idGroup)

                notifyId.cancelNotification(notifier)

                channel?.handleCancel(context, group, notifyId, data)
            }
        }

        if (groupId != null && channelId != null)
            refreshSummaryOf(context, groupId, channelId)
        else refreshSummaries(context)

        return notifyMap
    }
}
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

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.notifications.manager.save.NotificationsData
import eu.codetopic.utils.notifications.manager.data.NotificationId
import eu.codetopic.utils.notifications.manager.util.NotificationChannel
import eu.codetopic.utils.notifications.manager.util.SummarizedNotificationGroup
import eu.codetopic.utils.ids.Identifiers.Companion.nextId
import eu.codetopic.utils.notifications.manager.receiver.NotificationDeleteReceiver
import eu.codetopic.utils.notifications.manager.receiver.NotificationLaunchReceiver
import eu.codetopic.utils.notifications.manager.util.NotificationGroup

/**
 * @author anty
 */
internal object Notifications {

    private const val LOG_TAG = "Notifications"

    private fun prepareNotification(context: Context, id: NotificationId,
                                    notification: NotificationCompat.Builder) {
        notification
                .setGroup(id.groupId)
                .setGroupSummary(id.isSummary)
                .setOnlyAlertOnce(true)
                .setGroupAlertBehavior(
                        if (id.isSummary) NotificationCompat.GROUP_ALERT_SUMMARY
                        else NotificationCompat.GROUP_ALERT_ALL
                )
                .setWhen(id.whenTime)
                .setContentIntent(
                        PendingIntent.getBroadcast(
                                context,
                                NotificationLaunchReceiver.REQUEST_CODE_TYPE.nextId(),
                                NotificationLaunchReceiver.getStartIntent(context, id),
                                PendingIntent.FLAG_UPDATE_CURRENT// or PendingIntent.FLAG_ONE_SHOT
                        )
                )
                .setDeleteIntent(
                        PendingIntent.getBroadcast(
                                context,
                                NotificationDeleteReceiver.REQUEST_CODE_TYPE.nextId(),
                                NotificationDeleteReceiver.getStartIntent(context, id),
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
                        )
                )
    }

    private fun createSummaryNotification(context: Context,
                                          group: SummarizedNotificationGroup,
                                          channel: NotificationChannel,
                                          id: NotificationId,
                                          data: Map<NotificationId, Bundle>): Notification {
        return group.createSummaryNotification(context, id, channel, data)
                .also { prepareNotification(context, id, it) }
                .build()
    }

    private fun createNotification(context: Context,
                                   id: NotificationId,
                                   data: Bundle): Notification =
            createNotification(
                    context,
                    NotificationsGroups[id.groupId],
                    NotificationsChannels[id.channelId],
                    id, data
            )

    private fun createNotification(context: Context,
                                   group: NotificationGroup,
                                   channel: NotificationChannel,
                                   id: NotificationId,
                                   data: Bundle): Notification {
        return group.createNotification(context, id, channel, data)
                .also { prepareNotification(context, id, it) }
                .build()
    }

    private fun showNotification(notifier: NotificationManagerCompat,
                                 id: NotificationId, notification: Notification) {
        try {
            notifier.notify(id.tag, id.id, notification)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "showNotification(id=$id) -> Failed to show notification", e)
        }
    }

    private fun cancelNotification(notifier: NotificationManagerCompat,
                                   id: NotificationId) {
        try {
            notifier.cancel(id.tag, id.id)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "cancelNotification(id=$id) -> Failed to cancel notification", e)
        }
    }

    internal fun refresh(context: Context) {
        val notifier = NotificationManagerCompat.from(context)
        NotificationsData.instance.getAll().forEach {
            showNotification(notifier, it.key,
                    createNotification(context, it.key, it.value))
        }

        refreshSummaries(context)
    }

    private fun refreshSummary(context: Context,
                               notifier: NotificationManagerCompat,
                               group: SummarizedNotificationGroup,
                               channel: NotificationChannel,
                               data: Map<NotificationId, Bundle>?) {
        try {
            val nId = NotificationId.newSummary(group.id, channel.id)
            if (data != null) {
                showNotification(notifier, nId,
                        createSummaryNotification(context, group, channel, nId, data))
            } else cancelNotification(notifier, nId)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "refreshSummary() -> " +
                    "(groupId=${group.id}, channelId=${channel.id}) -> " +
                    "Failed to refresh group's channel notification", e)
        }
    }

    private fun refreshSummary(context: Context, notifier: NotificationManagerCompat,
                               group: NotificationGroup, channel: NotificationChannel) {
        if (group !is SummarizedNotificationGroup) return

        refreshSummary(
                context, notifier, group, channel,
                NotificationsData.instance.getAll(group.id, channel.id)
                        .takeIf { it.isNotEmpty() }
        )
    }

    private fun refreshSummary(context: Context, notifier: NotificationManagerCompat,
                               groupId: String, channelId: String) {
        refreshSummary(
                context, notifier,
                NotificationsGroups[groupId],
                NotificationsChannels[channelId]
        )
    }

    private fun refreshSummaryOf(context: Context, notifier: NotificationManagerCompat,
                                 id: NotificationId) {
        refreshSummary(context, notifier, id.groupId, id.channelId)
    }

    internal fun refreshSummaryOf(context: Context, id: NotificationId) {
        refreshSummaryOf(context, NotificationManagerCompat.from(context), id)
    }

    internal fun refreshSummaries(context: Context) {
        val notifier = NotificationManagerCompat.from(context)
        val nData = NotificationsData.instance.getAll()
        NotificationsGroups.getAll().forEach { group ->
            if (group !is SummarizedNotificationGroup) return@forEach
            try {
                NotificationsChannels.getAll().forEach { channel ->
                    refreshSummary(context, notifier, group, channel, nData
                            .filter { group.id == it.key.groupId && channel.id == it.key.channelId }
                            .takeIf { it.isNotEmpty() })
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "refreshSummaries() -> (groupId=${group.id}) -> " +
                        "Failed to refresh notifications of group's channels", e)
            }
        }
    }

    internal fun notify(context: Context, groupId: String, channelId: String, data: Bundle,
                        whenTime: Long = System.currentTimeMillis()): NotificationId {
        val group = NotificationsGroups[groupId]
        val channel = NotificationsChannels[channelId]

        val notifier = NotificationManagerCompat.from(context)

        return NotificationsData.instance.put(context, groupId, channelId, data, whenTime)
                .also { id ->
                    showNotification(notifier, id,
                            createNotification(context, group, channel, id, data))
                    refreshSummary(context, notifier, group, channel)
                }
    }

    internal fun notifyAll(context: Context, groupId: String, channelId: String, data: List<Bundle>,
                           whenTime: Long = System.currentTimeMillis()): List<NotificationId> {
        val group = NotificationsGroups[groupId]
        val channel = NotificationsChannels[channelId]

        val notifier = NotificationManagerCompat.from(context)
        return NotificationsData.instance.putAll(context, groupId, channelId, data, whenTime)
                .onEach {
                    showNotification(notifier, it.key,
                            createNotification(context, group, channel, it.key, it.value))
                }
                .also {
                    refreshSummary(context, notifier, group, channel)
                }.keys.toList()
    }

    internal fun cancel(context: Context, id: NotificationId): Bundle? {
        val notifier = NotificationManagerCompat.from(context)

        return NotificationsData.instance.remove(id)?.apply {
            cancelNotification(notifier, id)
            refreshSummaryOf(context, notifier, id)
        } ?: run {
            Log.e(LOG_TAG, "cancel(id=$id) -> Notification doesn't exists",
                    IllegalArgumentException("Notification doesn't exists")); null
        }
    }

    internal fun cancelAll(context: Context, ids: List<NotificationId>): Map<NotificationId, Bundle> {
        val notifier = NotificationManagerCompat.from(context)

        return NotificationsData.instance.removeAll(ids).onEach {
            cancelNotification(notifier, it.key)
        }.also { refreshSummaries(context) }
    }

    internal fun cancelAll(context: Context, groupId: String? = null,
                           channelId: String? = null): Map<NotificationId, Bundle> {
        val notifier = NotificationManagerCompat.from(context)

        return NotificationsData.instance.removeAll(groupId, channelId).onEach {
            cancelNotification(notifier, it.key)
        }.also { refreshSummaries(context) }
    }

    internal fun cleanup(context: Context) = cancelAll(
            context,
            NotificationsData.instance.getAll()
                    .filter {
                        it.key.groupId !in NotificationsGroups ||
                                it.key.channelId !in NotificationsChannels
                    }
                    .map { it.key }
    )
}
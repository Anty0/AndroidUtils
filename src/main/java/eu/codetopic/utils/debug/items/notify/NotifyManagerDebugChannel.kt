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

package eu.codetopic.utils.debug.items.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import eu.codetopic.java.utils.JavaExtensions.alsoIfNull
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.utils.R
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.ids.Identifiers.Companion.nextId
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.util.NotifyChannel
import eu.codetopic.utils.notifications.manager.util.NotifyChannel.Companion.combinedIdFor
import eu.codetopic.utils.notifications.manager.util.NotifyGroup
import eu.codetopic.utils.notifications.manager.util.SummarizedNotifyChannel
import kotlinx.serialization.json.JSON
import org.jetbrains.anko.longToast

/**
 * @author anty
 */
class NotifyManagerDebugChannel : NotifyChannel(ID, true) {

    companion object {

        private const val LOG_TAG = "NotifyManagerDebugChannel"
        const val ID = "eu.codetopic.utils.debug.items.notify.$LOG_TAG"

        private val idType = Identifiers.Type(ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createChannel(context: Context, combinedId: String): NotificationChannel =
            android.app.NotificationChannel(
                    combinedId,
                    context.getText(R.string.debug_item_notify_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
                enableVibration(true)
                setBypassDnd(false)
                setShowBadge(true)
                this.lightColor = ContextCompat.getColor(context, R.color.materialYellow)
            }

    override fun nextId(context: Context, group: NotifyGroup,
                        data: Bundle): Int = idType.nextId()

    override fun handleContentIntent(context: Context, group: NotifyGroup,
                                     notifyId: NotifyId, data: Bundle) {
        context.longToast(R.string.debug_item_notify_notification_click_toast_text)
    }

    override fun handleDeleteIntent(context: Context, group: NotifyGroup,
                                    notifyId: NotifyId, data: Bundle) {
        context.longToast(R.string.debug_item_notify_notification_delete_toast_text)
    }

    override fun handleCancel(context: Context, group: NotifyGroup?,
                              notifyId: NotifyId, data: Bundle?) {
        context.longToast(R.string.debug_item_notify_notification_cancel_toast_text)
    }

    private fun buildNotificationBase(context: Context,
                                      group: NotifyGroup): NotificationCompat.Builder =
            NotificationCompat.Builder(context, combinedIdFor(group)).apply {
                //setContentTitle()
                //setContentText()
                //setSubText()
                //setTicker()
                //setUsesChronometer()
                //setNumber()
                //setShowWhen(true)
                //setStyle()

                setSmallIcon(R.drawable.ic_notify_bug)
                /*setLargeIcon(
                        context.getIconics(GoogleMaterial.Icon.gmd_warning)
                                .sizeDp(24)
                                .colorRes(R.color.materialRed)
                                .toBitmap()
                )*/
                color = ContextCompat.getColor(context, R.color.materialYellow)
                setColorized(true)

                setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                setDefaults(NotificationCompat.DEFAULT_ALL)
                priority = NotificationCompat.PRIORITY_DEFAULT

                setAutoCancel(true)
                setCategory(NotificationCompat.CATEGORY_EVENT)

                setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                //setTimeoutAfter()
                //setOngoing()
                //setPublicVersion()

                //addAction()
            }

    override fun createNotification(context: Context, group: NotifyGroup,
                                    notifyId: NotifyId, data: Bundle): NotificationCompat.Builder =
            buildNotificationBase(context, group).apply {
                setContentTitle(context.getText(R.string.debug_item_notify_notification_title))
                setContentText(context.getText(R.string.debug_item_notify_notification_text))
            }
}
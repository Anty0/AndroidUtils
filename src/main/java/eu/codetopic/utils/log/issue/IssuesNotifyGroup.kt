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

package eu.codetopic.utils.log.issue

import android.app.NotificationChannelGroup
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.DEFAULT_LIGHTS
import android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE
import android.support.v4.content.ContextCompat
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.java.utils.JavaExtensions.alsoIfNull
import eu.codetopic.utils.AndroidExtensions.getIconics
import eu.codetopic.utils.R
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.ids.Identifiers.Companion.nextId
import eu.codetopic.utils.notifications.manager.data.NotificationId
import eu.codetopic.utils.notifications.manager.util.NotificationChannel
import eu.codetopic.utils.notifications.manager.util.SummarizedNotificationGroup
import kotlinx.serialization.json.JSON

/**
 * @author anty
 */
class IssuesNotifyGroup : SummarizedNotificationGroup(ID, true) {

    companion object {

        private const val LOG_TAG = "IssuesNotifyGroup"
        const val ID = "eu.codetopic.utils.log.issue.$LOG_TAG"

        private val idType = Identifiers.Type(ID)

        private const val PARAM_ISSUE = "ISSUE"

        fun dataFor(issue: Issue): Bundle = Bundle().apply {
            putString(PARAM_ISSUE, JSON.stringify(issue))
        }

        fun readData(data: Bundle): Issue? =
                data.getString(PARAM_ISSUE)?.let { JSON.parse(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createGroup(context: Context): NotificationChannelGroup =
            NotificationChannelGroup(id, LOG_TAG) // TODO: better name

    override fun nextId(context: Context, channel: NotificationChannel,
                        data: Bundle): Int = idType.nextId()

    override fun handleContentIntent(context: Context, id: NotificationId,
                                     channel: NotificationChannel, data: Bundle) {
        IssueInfoActivity.start(
                context,
                id,
                readData(data)
                        ?: throw IllegalArgumentException("Data doesn't contains log line")
        )
    }

    override fun handleSummaryContentIntent(context: Context, id: NotificationId,
                                            channel: NotificationChannel,
                                            data: Map<NotificationId, Bundle>) {
        IssuesActivity.start(context)
    }

    private fun buildNotificationBase(context: Context,
                                      channel: NotificationChannel): NotificationCompat.Builder =
            NotificationCompat.Builder(context, channel.id).apply {
                //setContentTitle()
                //setContentText()
                //setSubText()
                //setTicker()
                //setUsesChronometer()
                //setNumber()
                //setShowWhen(true)
                //setStyle()

                setSmallIcon(android.R.drawable.stat_sys_warning)
                setLargeIcon(
                        context.getIconics(GoogleMaterial.Icon.gmd_warning)
                                .sizeDp(24)
                                .colorRes(R.color.materialRed)
                                .toBitmap()
                )
                color = ContextCompat.getColor(context, R.color.materialRed)
                setColorized(true)

                setDefaults(DEFAULT_VIBRATE or DEFAULT_LIGHTS)

                setAutoCancel(false) // will be canceled by IssueInfoActivity by user
                setCategory(NotificationCompat.CATEGORY_ERROR)

                setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                //setTimeoutAfter()
                //setOngoing()
                //setPublicVersion()

                //addAction()
                extend(NotificationCompat.WearableExtender()
                        .setHintContentIntentLaunchesActivity(true))
            }

    override fun createNotification(context: Context,
                                    id: NotificationId,
                                    channel: NotificationChannel,
                                    data: Bundle): NotificationCompat.Builder =
            buildNotificationBase(context, channel).apply {
                val issue = readData(data)
                        ?: throw IllegalArgumentException("Data doesn't contains log line")
                val isError = issue.priority == Priority.ERROR

                setContentTitle(context.getText(
                        if (isError) R.string.notify_logged_error_title
                        else R.string.notify_logged_warning_title
                ))
                setContentText(issue.toString(false))
                setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText(issue.toString(true))
                )
            }

    override fun createSummaryNotification(context: Context,
                                           id: NotificationId,
                                           channel: NotificationChannel,
                                           data: Map<NotificationId, Bundle>): NotificationCompat.Builder {
        val allIssues = data.values.mapNotNull {
            readData(it).alsoIfNull {
                Log.w(LOG_TAG, "Data doesn't contains log line")
            }
        }

        return buildNotificationBase(context, channel).apply {
            setContentTitle(context.getText(R.string.notify_logged_summary_title))
            setContentText(context.getText(R.string.notify_logged_summary_text))
            setNumber(allIssues.size)
            setStyle(
                    NotificationCompat.InboxStyle()
                            .setBigContentTitle(context.getText(R.string.notify_logged_summary_title))
                            .setSummaryText(context.getText(R.string.notify_logged_summary_text))
                            .also { n ->
                                allIssues.forEach {
                                    n.addLine(it.toString(false))
                                }
                            }
            )
        }
    }
}
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

package eu.codetopic.utils.log.issue.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import eu.codetopic.java.utils.alsoIfNull
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.base.Priority.*
import eu.codetopic.utils.R
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.ids.Identifiers.Companion.nextId
import eu.codetopic.utils.log.issue.data.Issue
import eu.codetopic.utils.log.issue.ui.IssueInfoActivity
import eu.codetopic.utils.log.issue.ui.IssuesActivity
import eu.codetopic.utils.notifications.manager.combinedIdFor
import eu.codetopic.utils.notifications.manager.data.NotifyId
import eu.codetopic.utils.notifications.manager.util.NotifyGroup
import eu.codetopic.utils.notifications.manager.util.SummarizedNotifyChannel
import kotlinx.serialization.json.JSON

/**
 * @author anty
 */
class IssuesNotifyChannel : SummarizedNotifyChannel(ID, checkForIdOverrides = true) {

    companion object {

        private const val LOG_TAG = "IssuesNotifyChannel"
        const val ID = "eu.codetopic.utils.log.issue.notify.channel"

        private val idType = Identifiers.Type(ID)

        private const val PARAM_ISSUE = "ISSUE"

        fun dataFor(issue: Issue): Bundle = Bundle().apply {
            putString(PARAM_ISSUE, JSON.stringify(issue))
        }

        fun readData(data: Bundle): Issue? =
                data.getString(PARAM_ISSUE)?.let { JSON.parse(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createChannel(context: Context, combinedId: String): NotificationChannel =
            android.app.NotificationChannel(
                    combinedId,
                    context.getText(R.string.notify_issues_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                setBypassDnd(false)
                setShowBadge(true)
                this.lightColor = ContextCompat.getColor(context, R.color.materialRed)
            }

    override fun nextId(context: Context, group: NotifyGroup,
                        data: Bundle): Int = idType.nextId()

    override fun handleContentIntent(context: Context, group: NotifyGroup,
                                     notifyId: NotifyId, data: Bundle) {
        IssueInfoActivity.start(
                context = context,
                notifyId = notifyId,
                issue = readData(data)
                        ?: throw IllegalArgumentException("Data doesn't contains issue")
        )
    }

    override fun handleSummaryContentIntent(context: Context, group: NotifyGroup,
                                            notifyId: NotifyId, data: Map<out NotifyId, Bundle>) {
        context.startActivity(
                IssuesActivity.getIntent(context)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
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
                color = ContextCompat.getColor(context, R.color.materialRed)
                setColorized(true)

                setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.DEFAULT_LIGHTS)
                priority = NotificationCompat.PRIORITY_HIGH

                setAutoCancel(false) // will be canceled by IssueInfoActivity by user
                setCategory(NotificationCompat.CATEGORY_ERROR)

                setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                //setTimeoutAfter()
                //setOngoing()
                //setPublicVersion()

                //addAction()
            }

    override fun createNotification(context: Context, group: NotifyGroup,
                                    notifyId: NotifyId, data: Bundle): NotificationCompat.Builder =
            buildNotificationBase(context, group).apply {
                val issue = readData(data)
                        ?: throw IllegalArgumentException("Data doesn't contains issue")

                setContentTitle(context.getText(
                        when (issue.priority) {
                            ERROR -> R.string.notify_logged_error_title
                            WARN -> R.string.notify_logged_warning_title
                            BREAK_EVENT -> R.string.notify_logged_break_event_title
                            else -> R.string.notify_logged_issue_title
                        }
                ))
                setContentText(issue.toString(false))
                setStyle(
                        NotificationCompat.BigTextStyle()
                                .bigText(issue.toString(true))
                )
            }

    override fun createSummaryNotification(context: Context, group: NotifyGroup,
                                           notifyId: NotifyId, data: Map<out NotifyId, Bundle>): NotificationCompat.Builder {
        val allIssues = data.values.mapNotNull {
            readData(it).alsoIfNull {
                Log.e(LOG_TAG, "Data doesn't contains issue")
            }
        }

        val title = context.getText(R.string.notify_logged_summary_title)
        val text = context.getText(R.string.notify_logged_summary_text)

        return buildNotificationBase(context, group).apply {
            setContentTitle(title)
            setContentText(text)
            setNumber(allIssues.size)
            setStyle(
                    NotificationCompat.InboxStyle()
                            .setSummaryText(title)
                            .setBigContentTitle(title)
                            .also { n ->
                                allIssues.forEach {
                                    n.addLine(it.toString(false))
                                }
                            }
            )
        }
    }
}
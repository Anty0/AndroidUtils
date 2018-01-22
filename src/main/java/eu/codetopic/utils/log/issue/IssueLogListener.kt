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

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.support.annotation.MainThread
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.Logger
import eu.codetopic.java.utils.log.LogsHandler
import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.utils.UtilsBase
import eu.codetopic.utils.log.issue.Issue.Companion.toIssue
import eu.codetopic.utils.notifications.manager.NotificationsManager
import eu.codetopic.utils.thread.LooperUtils

/**
 * @author anty
 */
class IssueLogListener private constructor(private val appContext: Context) :
        LogsHandler.OnLoggedListener {

    companion object {

        private const val LOG_TAG = "IssueLogListener"

        private const val FATAL_EXCEPTION_LOG_TAG = "FatalExceptionHandler"

        private val BLOCKLIST = arrayOf(
                LOG_TAG
        )

        private val BLACKLIST = arrayOf(
                "IssuesNotifyGroup",
                "Notifications",
                "NotificationsData"
        ) // TODO: check if all required classes are in this blacklist

        private var initialized = false

        @MainThread
        @Synchronized
        fun initialize(context: Context) {
            if (!DebugMode.isEnabled || initialized) return
            initialized = true

            // create listener
            val listener = IssueLogListener(context.applicationContext)

            // override default uncaught (fatal) exception handler
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
                listener.onLogged(
                        LogLine(Priority.ERROR, FATAL_EXCEPTION_LOG_TAG, null, ex)
                )
                defaultHandler.uncaughtException(thread, ex)
            }

            // add listener to logs handler
            Logger.logsHandler.addOnLoggedListener(listener)

            // initialize notifications and enable IssuesActivity, but only on primary process
            if (UtilsBase.ACTIVE_PROFILE.isPrimaryProcess(context)) {
                NotificationsManager.initChannel(context, IssuesNotifyChannel())
                NotificationsManager.initGroup(context, IssuesNotifyGroup())
            }
        }
    }

    override fun onLogged(logLine: LogLine) {
        if (!DebugMode.isEnabled) return

        try {
            when (logLine.tag) {
                in BLOCKLIST -> {} // ignore
                in BLACKLIST -> {
                    // If log line is from classes used by this IssueLogListener,
                    //  show only IssueInfoActivity directly.
                    // This protects application from error logging loop.
                    LooperUtils.runOnMainThread {
                        IssueInfoActivity.start(
                                context = appContext,
                                id = null,
                                issue = logLine.toIssue()
                        )
                    }
                }
                else -> {
                    // Safe log line. Let's show notification.
                    NotificationsManager.requestNotify(
                            context = appContext,
                            groupId = IssuesNotifyGroup.ID,
                            channelId = IssuesNotifyChannel.ID,
                            data = IssuesNotifyGroup.dataFor(logLine.toIssue()),
                            optimise = logLine.tag == FATAL_EXCEPTION_LOG_TAG
                            // If this is fatal exception, process
                            //  notification right now (because app will be killed).
                    )
                }
            }
        } catch (t: Throwable) {
            Log.e(LOG_TAG, "Failed to process logged issue", t)
        }
    }

    override val filterPriorities: Array<Priority>?
        get() = arrayOf(Priority.WARN, Priority.ERROR)
}
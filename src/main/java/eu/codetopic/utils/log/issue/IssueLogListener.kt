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
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager2.create.NotificationBuilder.Companion.requestShow
import eu.codetopic.utils.thread.LooperUtils

/**
 * @author anty
 */
class IssueLogListener private constructor(private val appContext: Context) :
        LogsHandler.OnLoggedListener, Thread.UncaughtExceptionHandler {

    companion object {

        private const val LOG_TAG = "IssueLogListener"

        private const val FATAL_EXCEPTION_LOG_TAG = "FatalExceptionHandler"

        private val LIST_IGNORE = arrayOf(
                LOG_TAG
        )

        private val LIST_NO_NOTIFICATION = arrayOf(
                "Notifier",
                "NotifyData",
                "IssuesNotifyGroup",
                "IssuesNotifyChannel",
                "RqNotifyAllReceiver",
                "RqNotifyReceiver"
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
            Thread.setDefaultUncaughtExceptionHandler { thread, thr ->
                listener.uncaughtException(thread, thr)
                defaultHandler.uncaughtException(thread, thr)
            }

            // add listener to logs handler
            Logger.logsHandler.addOnLoggedListener(listener)

            // initialize notifications
            NotifyManager.installGroup(context, IssuesNotifyGroup())
            NotifyManager.installChannel(context, IssuesNotifyChannel())
        }
    }

    override fun uncaughtException(thread: Thread, thr: Throwable) {
        onLogged(LogLine(Priority.ERROR, FATAL_EXCEPTION_LOG_TAG, null, thr))
    }

    override fun onLogged(logLine: LogLine) {
        if (!DebugMode.isEnabled) return

        try {
            when (logLine.tag) {
                in LIST_IGNORE -> {} // ignore
                in LIST_NO_NOTIFICATION -> {
                    // If log line is from classes used by this IssueLogListener,
                    //  show only IssueInfoActivity directly.
                    // This protects application from error logging loop.
                    LooperUtils.runOnMainThread {
                        IssueInfoActivity.start(
                                context = appContext,
                                notifyId = null,
                                issue = logLine.toIssue()
                        )
                    }
                }
                else -> {
                    // Safe log line. Let's show notification.

                    NotificationBuilder.create(
                            groupId = IssuesNotifyGroup.ID,
                            channelId = IssuesNotifyChannel.ID,
                            init = {
                                persistent = true
                                refreshable = true
                                data = IssuesNotifyChannel.dataFor(logLine.toIssue())
                            }
                    ).requestShow(
                            appContext,
                            optimise = logLine.tag == FATAL_EXCEPTION_LOG_TAG
                            // If this is fatal exception, allow processing
                            // of notification right now (because app may be killed),
                            // but if not, disallow optimisation (executing on same thread).
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
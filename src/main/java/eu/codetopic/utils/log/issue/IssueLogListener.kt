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

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.MainThread
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.Logger
import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.Priority.*
import eu.codetopic.utils.log.issue.data.Issue.Companion.toIssue
import eu.codetopic.utils.log.issue.notify.IssuesNotifyChannel
import eu.codetopic.utils.log.issue.notify.IssuesNotifyGroup
import eu.codetopic.utils.log.issue.ui.IssueInfoActivity
import eu.codetopic.utils.notifications.manager.NotifyBase
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.create.NotificationBuilder
import eu.codetopic.utils.notifications.manager.show
import eu.codetopic.utils.thread.LooperUtils

/**
 * @author anty
 */
class IssueLogListener private constructor(val appContext: Context) {

    companion object {

        private const val LOG_TAG = "IssueLogListener"
        private const val FATAL_EXCEPTION_LOG_TAG = "FatalExceptionHandler"

        private val ISSUE_PRIORITIES = arrayOf(BREAK_EVENT, WARN, ERROR)

        private val LIST_IGNORE = arrayOf(
                LOG_TAG
        )
        private val LIST_NO_NOTIFICATION = arrayOf(
                "Notifier",
                "NotifyManager",
                "NotifyData",
                "IssuesNotifyGroup",
                "IssuesNotifyChannel",
                "RqNotifyAllReceiver",
                "RqNotifyReceiver"
        )

        @SuppressLint("StaticFieldLeak")
        private lateinit var INSTANCE: IssueLogListener

        private val LOGGED_LISTENER: (LogLine) -> Unit = onLogged@ {
            if (it.priority !in ISSUE_PRIORITIES) return@onLogged
            INSTANCE.processLogLine(it)
        }

        private lateinit var DEFAULT_UNCAUGHT_HANDLER: Thread.UncaughtExceptionHandler
        private val UNCAUGHT_HANDLER = Thread.UncaughtExceptionHandler handler@ { thread, thr ->
            INSTANCE.processLogLine(
                    LogLine(ERROR, FATAL_EXCEPTION_LOG_TAG, null, thr)
            )
            DEFAULT_UNCAUGHT_HANDLER.uncaughtException(thread, thr)
        }

        private val DEBUG_MODE_CHANGED_LISTENER = {
            if (DebugMode.isEnabled) {
                if (!NotifyManager.hasGroup(IssuesNotifyGroup.ID))
                    NotifyManager.installGroup(INSTANCE.appContext, IssuesNotifyGroup())
                if (!NotifyManager.hasChannel(IssuesNotifyChannel.ID))
                    NotifyManager.installChannel(INSTANCE.appContext, IssuesNotifyChannel())
            } else {
                if (NotifyManager.hasGroup(IssuesNotifyGroup.ID))
                    NotifyManager.uninstallGroup(INSTANCE.appContext, IssuesNotifyGroup.ID)
                if (NotifyManager.hasChannel(IssuesNotifyChannel.ID))
                    NotifyManager.uninstallChannel(INSTANCE.appContext, IssuesNotifyChannel.ID)
            }
        }

        private var initialized = false

        @MainThread
        @Synchronized
        fun initialize(context: Context) {
            if (initialized) return
            initialized = true

            // create listener
            INSTANCE = IssueLogListener(context.applicationContext)

            // override default uncaught (fatal) exception handler
            DEFAULT_UNCAUGHT_HANDLER = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_HANDLER)

            // add listener to logs handler
            Logger.logsHandler.addOnLoggedListener(LOGGED_LISTENER)

            // add DebugMode change listener
            DebugMode.addChangeListener(DEBUG_MODE_CHANGED_LISTENER)
            DEBUG_MODE_CHANGED_LISTENER()
        }
    }

    fun processLogLine(logLine: LogLine) {
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
                    ).show(
                            appContext,
                            optimise = logLine.tag == FATAL_EXCEPTION_LOG_TAG
                                    && NotifyBase.isPostInitCleanupDone
                            // If this is fatal exception, allow processing
                            // of notification right now (because app may be killed),
                            // but if not, disallow optimisation (executing on same thread).
                    )
                }
            }
        } catch (t: Throwable) {
            Log.e(LOG_TAG, "Failed to process log line", t)
        }
    }
}
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

package eu.codetopic.utils

import android.app.Application
import android.support.annotation.UiThread

import com.squareup.leakcanary.LeakCanary

import java.util.Arrays

import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.broadcast.BroadcastsConnector
import eu.codetopic.utils.broadcast.LocalBroadcast
import eu.codetopic.utils.network.NetworkManager
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.log.AndroidLoggerExtension
import eu.codetopic.utils.notifications.manager.NotificationsManager
import eu.codetopic.utils.thread.LooperUtils

@UiThread
object UtilsBase {

    private val LOG_TAG = "UtilsBase"

    private var profile: ProcessProfile? = null

    val ACTIVE_PROFILE: ProcessProfile get() = profile ?:
            throw IllegalStateException("$LOG_TAG is not initialized")

    fun initialize(app: Application, vararg profiles: ProcessProfile) {
        if (profile != null) throw IllegalStateException("$LOG_TAG is still initialized")

        val processName = with(AndroidUtils.getCurrentProcessName()) {
            if (this == null) {
                Log.e(LOG_TAG, "initialize", RuntimeException(
                        "Can't get CurrentProcessName, using main process name"))
                app.packageName
            } else this
        }

        val providersProfile = ProcessProfile("${app.packageName}:providers", true)
        val leakCanaryProfile = ProcessProfile("${app.packageName}:leakcanary", false)

        profile = arrayOf(*profiles).plus(arrayOf(providersProfile, leakCanaryProfile))
                .firstOrNull { processName == it.processName } ?: run {
            Log.e(LOG_TAG, "initialize(app=$app, profiles=$profiles)", IllegalStateException("Can't find processName ("
                + processName + "), in provided ProcessProfiles, using empty ProcessProfile (utils will be disabled)"))
            ProcessProfile(processName, false)
        }

        completeInit(app)
    }

    private fun completeInit(app: Application) {
        android.util.Log.d(AndroidUtils.getAppLabel(app).toString(), "INITIALIZING:"
                + "\n    - PROCESS_PROFILE=" + ACTIVE_PROFILE
                + "\n    - DEBUG=" + BuildConfig.DEBUG
                + "\n    - BUILD_TYPE=" + BuildConfig.BUILD_TYPE
                + "\n    - VERSION_NAME=" + AndroidUtils.getApplicationVersionName(app)
                + "\n    - VERSION_CODE=" + AndroidUtils.getApplicationVersionCode(app))

        if (ACTIVE_PROFILE.initializeUtils) {
            if (!LeakCanary.isInAnalyzerProcess(app)) {
                LeakCanary.install(app)
            }

            // Initialize logger
            AndroidLoggerExtension.install(app)

            // Initialize some util classes
            LocalBroadcast.initialize(app)
            NetworkManager.init(app)
            LooperUtils.initialize(app)
            BroadcastsConnector.initialize(app)
            Identifiers.initialize(app)

            // Add callback listening onLowMemory event
            /*app.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {

                }

                override fun onLowMemory() {
                    System.runFinalization()
                    System.gc()
                }
            })*/
        }

        ACTIVE_PROFILE.additionalCommands.forEach { it.run() }
    }

    /**
     * Methods that should be called in additionalCommands:
     * - `LocaleManager.initialize() `
     * - `eu.codetopic.java.utils.debug.DebugMode.setEnabled() (or in kotlin DebugBode.isEnabled = ?) `
     * - `eu.codetopic.java.utils.log.LogsHandler.addOnLoggedListener() ` using `Logger.getLogsHandler (or in kotlin Logger.logsHandler) `
     * - `eu.codetopic.utils.broadcast.BroadcastsConnector.connect() `
     */
    class ProcessProfile(val processName: String, val initializeUtils: Boolean, vararg val additionalCommands: Runnable) {

        override fun toString(): String {
            return "ProcessProfile(processName='$processName', " +
                    "initializeUtils=$initializeUtils, " +
                    "additionalCommands=${Arrays.toString(additionalCommands)})"
        }
    }

}

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
import android.content.Context
import android.os.Bundle
import android.support.annotation.UiThread
import com.squareup.leakcanary.LeakCanary
import eu.codetopic.java.utils.letIfNull
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.broadcast.BroadcastsConnector
import eu.codetopic.utils.broadcast.LocalBroadcast
import eu.codetopic.utils.debug.AndroidDebugModeExtension
import eu.codetopic.utils.debug.items.notify.NotifyManagerDebugItem
import eu.codetopic.utils.ids.Identifiers
import eu.codetopic.utils.log.AndroidLoggerExtension
import eu.codetopic.utils.network.NetworkManager
import eu.codetopic.utils.notifications.manager.NotifyBase
import eu.codetopic.utils.thread.LooperUtils
import org.jetbrains.anko.bundleOf

@UiThread
object UtilsBase {

    private const val LOG_TAG = "UtilsBase"

    const val PROCESS_NAME_PROVIDERS = ":providers"
    const val PROCESS_NAME_NOTIFY_MANAGER = ":notify"
    const val PROCESS_NAME_LEAKCANARY = ":leakcanary"

    const val PARAM_INITIALIZE_UTILS = "$LOG_TAG.INITIALIZE_UTILS"

    val Context.processNamePrimary: String
        get() = applicationContext.packageName

    val Context.processNameProviders: String
        get() = processNamePrimary + PROCESS_NAME_PROVIDERS

    val Context.processNameNotifyManager: String
        get() = processNamePrimary + PROCESS_NAME_NOTIFY_MANAGER

    val Context.processNameLeakcanary: String
        get() = processNamePrimary + PROCESS_NAME_LEAKCANARY

    object Process {

        private const val LOG_TAG = "${UtilsBase.LOG_TAG}.UtilsBase"

        private var initialized: Boolean = false
        private val paramsMap: MutableMap<String, Bundle> = mutableMapOf()
        private var currProcName: String? = null

        val isInitialized: Boolean
            get() = initialized

        fun initialize(app: Application) {
            if (isInitialized) throw IllegalStateException("$LOG_TAG is still initialized")
            initialized = true
            currProcName = AndroidUtils.getCurrentProcessName(app).letIfNull {
                Log.e(LOG_TAG, "initialize", RuntimeException(
                        "Can't obtain current process name, using main process name"))
                return@letIfNull app.processNamePrimary
            }
        }

        val name: String
            get() = currProcName ?: throw IllegalStateException("$LOG_TAG is not initialized")

        val params: Bundle
            get() = paramsOf(name)

        fun isPrimaryProcess(context: Context): Boolean =
                context.processNamePrimary == name

        fun paramsOf(processName: String): Bundle =
                paramsMap[processName]?.let { Bundle(it) } ?: Bundle.EMPTY

        fun addParams(processName: String, params: Bundle) {
            paramsMap.getOrPut(processName, ::Bundle).putAll(params)
        }

        fun addParams(vararg nameToParam: Pair<String, Bundle>) {
            nameToParam.forEach { addParams(it.first, it.second) }
        }
    }

    private var prepared: Boolean = false
    private var initialized: Boolean = false
    private var utilsInitialized: Boolean = false

    val isPrepared: Boolean
        get() = prepared

    val isInitialized: Boolean
        get() = initialized

    val isUtilsInitialized: Boolean
        get() = utilsInitialized

    fun prepare(app: Application, initProcessParams: Process.() -> Unit) {
        if (isPrepared) throw IllegalStateException("$LOG_TAG is still prepared")
        prepared = true

        Process.addParams(
                app.processNameProviders to bundleOf(
                        PARAM_INITIALIZE_UTILS to true
                ),
                app.processNameNotifyManager to bundleOf(
                        PARAM_INITIALIZE_UTILS to true
                ),
                app.processNameLeakcanary to bundleOf(
                        PARAM_INITIALIZE_UTILS to false
                )
        )

        Process.initProcessParams()

        Process.initialize(app)
    }

    /**
     * Methods that should be called in init:
     * - `LocaleManager.initialize() `
     * - `eu.codetopic.java.utils.debug.DebugMode.setEnabled() (or in kotlin DebugBode.isEnabled = ?) `
     * - `eu.codetopic.java.utils.log.LogsHandler.addOnLoggedListener() ` using `Logger.getLogsHandler (or in kotlin Logger.logsHandler) `
     * - `eu.codetopic.utils.broadcast.BroadcastsConnector.connect() `
     * - `eu.codetopic.utils.notifications.manager.NotifyManager.install*() `
     */
    fun initialize(app: Application, init: (processName: String, processParams: Bundle) -> Unit) {
        if (isInitialized) throw IllegalStateException("$LOG_TAG is still initialized")
        initialized = true

        android.util.Log.d(AndroidUtils.getAppLabel(app).toString(), "INITIALIZING:"
                + "\n    - PROCESS_NAME=${Process.name}"
                + "\n    - DEBUG=${BuildConfig.DEBUG}"
                + "\n    - BUILD_TYPE=${BuildConfig.BUILD_TYPE}"
                + "\n    - VERSION_NAME=${AndroidUtils.getApplicationVersionName(app)}"
                + "\n    - VERSION_CODE=${AndroidUtils.getApplicationVersionCode(app)}"
        )


        val processName = Process.name
        val processParams = Process.params

        val initializeUtils = processParams.let {
            if (it.containsKey(PARAM_INITIALIZE_UTILS)) {
                return@let it.getBoolean(PARAM_INITIALIZE_UTILS, false)
            } else {
                Log.e(LOG_TAG, "initialize()" +
                        " -> Parameter $LOG_TAG.PARAM_INITIALIZE_UTILS not found in" +
                        " current process params, utils won't be initialized")
                return@let false
            }
        }

        utilsInitialized = initializeUtils

        if (initializeUtils) {
            // Initialize JavaUtils's DebugMode
            AndroidDebugModeExtension.install(app)

            // Initialize JavaUtils's Logger
            AndroidLoggerExtension.install(app)

            // Install NotifyManagerDebugItem's notification channel and group
            NotifyManagerDebugItem.initialize(app)

            // Install LeakCanary
            LeakCanary.install(app)

            // Initialize some util classes
            LocalBroadcast.initialize(app)
            NetworkManager.init(app)
            LooperUtils.initialize(app)
            BroadcastsConnector.initialize(app)
            Identifiers.initialize(app)

            // Initialize NotifyManager
            NotifyBase.initialize(app)
        }

        init(processName, processParams)

        if (initializeUtils) {
            // Complete cleanup of NotifyManager
            NotifyBase.postInitCleanupAndRefresh(app)
        }
    }
}

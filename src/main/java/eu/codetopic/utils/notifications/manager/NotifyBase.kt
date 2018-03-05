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

import android.content.Context
import android.support.annotation.MainThread
import eu.codetopic.utils.UtilsBase
import eu.codetopic.utils.UtilsBase.processNameNotifyManager
import eu.codetopic.utils.notifications.manager.save.NotifyData

/**
 * @author anty
 */
object NotifyBase {

    private var postInitCleanupDone = false
    private var initialized = false
    private var usable = false

    val isPostInitCleanupDone: Boolean
        get() = postInitCleanupDone

    val isInitialized: Boolean
        get() = initialized

    val isUsable: Boolean
        get() = usable

    fun assertPostInitCleanupDone() {
        if (!isPostInitCleanupDone) throw IllegalStateException(
                "NotifyManager did not finished post init clean up in this process yet"
        )
    }

    fun assertInitialized(context: Context) {
        if (!isInitialized) throw IllegalStateException(
                "NotifyManager is not initialized in this process: " +
                        if (!isOnNotifyManagerProcess(context))
                            "Not running in ':notify' process"
                        else "Not yet initialized"
        )
    }

    fun assertUsable() {
        if (!isUsable) throw IllegalStateException(
                "NotifyManager is not usable in this process"
        )
    }

    fun isOnNotifyManagerProcess(context: Context) =
            context.processNameNotifyManager == UtilsBase.Process.name

    fun assertOnNotifyProcess(context: Context) {
        if (!isOnNotifyManagerProcess(context))
            throw IllegalStateException("Not running in ':notify' process")
    }

    @MainThread
    fun initialize(context: Context) {
        if (usable) throw IllegalStateException(
                "NotifyManager is still initialized in this process"
        )

        usable = true
        if (isOnNotifyManagerProcess(context)) initialized = true

        NotifyData.initialize(context)
    }

    @MainThread
    fun postInitCleanupAndRefresh(context: Context) {
        assertUsable()

        postInitCleanupDone = true

        if (isInitialized) {
            Notifier.cleanup(context)
        }

        // Don't refresh right now, only request it.
        // Refreshing right now can cause notifications,
        //  that was deleted by user to show and hide again,
        //  because their deletion will be processed after initialization of NotifyManager.
        NotifyManager.refresh(context, optimise = false)
    }
}
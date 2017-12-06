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

package eu.codetopic.utils.log

import android.content.Context
import android.support.annotation.MainThread

import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.Logger
import eu.codetopic.utils.BuildConfig

object AndroidLoggerExtension {

    private const val LOG_TAG = "AndroidLoggerExtension"

    private var INSTALLED = false

    @JvmStatic
    @MainThread
    @Synchronized
    fun install(context: Context) {
        if (INSTALLED) throw IllegalStateException("$LOG_TAG is still installed.")
        INSTALLED = true

        Log.debugMode = BuildConfig.DEBUG
        Logger.logTarget = AndroidLogTarget()

        Logger.logsHandler.addOnLoggedListener(
                ErrorInfoLogListener(context.applicationContext))
    }

}

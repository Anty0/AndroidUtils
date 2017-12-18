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
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.log.LogsHandler
import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.utils.thread.LooperUtils

/**
 * @author anty
 */
class ErrorInfoLogListener(private val appContext: Context) : LogsHandler.OnLoggedListener {

    override fun onLogged(logLine: LogLine) {
        if (!DebugMode.isEnabled) return

        LooperUtils.runOnMainThread { ErrorInfoActivity.start(appContext, logLine) }
    }

    override val filterPriorities: Array<Priority>?
        get() = arrayOf(Priority.WARN, Priority.ERROR)
}
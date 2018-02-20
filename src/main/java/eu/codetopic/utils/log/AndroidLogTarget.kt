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

import android.util.Log

import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.LogTarget
import eu.codetopic.java.utils.log.base.Priority
import eu.codetopic.java.utils.log.base.Priority.*
import kotlin.coroutines.experimental.buildSequence

class AndroidLogTarget : LogTarget {

    companion object {

        private const val SPLIT_LEN = 4000

        var splitTextToPartitions = true
    }

    override fun println(logLine: LogLine) {
        logLine.messageWithThrowable.let { message ->
            if (splitTextToPartitions && message.length > SPLIT_LEN) {
                buildSequence {
                    for (i in 0 until message.length step SPLIT_LEN) {
                        yield(message.substring(i, (i + SPLIT_LEN)
                                .takeIf { it <= message.length }
                                ?: message.length
                        ))
                    }
                }.toList()
            } else listOf(message)
        }.forEach { printLn(logLine, it) }
    }

    private fun printLn(logLine: LogLine, message: String) {
        android.util.Log.println(getPriorityId(logLine.priority), logLine.tag, message)
    }

    private fun getPriorityId(priority: Priority): Int =
            when (priority) {
                ASSERT -> Log.ASSERT
                DEBUG -> Log.DEBUG
                ERROR -> Log.ERROR
                INFO -> Log.INFO
                VERBOSE -> Log.VERBOSE
                BREAK_EVENT -> Log.WARN
                WARN -> Log.WARN
                else -> Log.ERROR
            }
}

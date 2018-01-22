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

import eu.codetopic.java.utils.log.base.LogLine
import eu.codetopic.java.utils.log.base.Priority
import kotlinx.serialization.Serializable

/**
 * @author anty
 */
@Serializable
class Issue(val priority: Priority, val tag: String,
            val message: String?, val throwableName: String?,
            val messageWithThrowable: String) {

    companion object {

        fun LogLine.toIssue(): Issue = Issue(this)
    }

    constructor(logLine: LogLine) :
            this(
                    priority = logLine.priority,
                    tag = logLine.tag,
                    message = logLine.message,
                    throwableName = logLine.throwable
                            ?.let { "${it.javaClass.name}: ${it.message}" },
                    messageWithThrowable = logLine.messageWithThrowable
            )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Issue

        if (priority != other.priority) return false
        if (tag != other.tag) return false
        if (message != other.message) return false
        if (messageWithThrowable != other.messageWithThrowable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = priority.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + messageWithThrowable.hashCode()
        return result
    }

    override fun toString() = toString(true)

    fun toString(showThrowable: Boolean) =
            "${priority.displayID}/$tag: " +
                    "${if (showThrowable) messageWithThrowable else message ?: throwableName}"
}
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

package eu.codetopic.utils.notifications.manager.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author anty
 */
@Serializable
class NotificationId private constructor(val isSummary: Boolean,
                                         val groupId: String,
                                         val channelId: String,
                                         val id: Int,
                                         val whenTime: Long) {

    companion object {

        private const val SUMMARY_NOTIFICATION_ID = 1

        internal fun newCommon(groupId: String, channelId: String, id: Int,
                               whenTime: Long = System.currentTimeMillis()) =
                NotificationId(false, groupId, channelId, id, whenTime)

        internal fun newSummary(groupId: String, channelId: String,
                               whenTime: Long = System.currentTimeMillis()) =
                NotificationId(true, groupId, channelId, SUMMARY_NOTIFICATION_ID, whenTime)

        internal fun tagFor(groupId: String, channelId: String, isSummary: Boolean) =
                "TAG(group=$groupId, channel=$channelId, isSummary=$isSummary)"
    }

    @Transient
    val tag: String
        get() = tagFor(groupId, channelId, isSummary)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationId

        if (groupId != other.groupId) return false
        if (channelId != other.channelId) return false
        if (id != other.id) return false
        if (isSummary != other.isSummary) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + id
        result = 31 * result + isSummary.hashCode()
        return result
    }

    override fun toString(): String =
            "NotificationId(groupId='$groupId', channelId='$channelId'," +
                    " id=$id, isSummary=$isSummary)"
}
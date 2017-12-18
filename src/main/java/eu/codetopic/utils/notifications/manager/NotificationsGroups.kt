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

package eu.codetopic.utils.notifications.manager

import android.content.Context
import eu.codetopic.utils.notifications.manager.util.NotificationGroup

/**
 * @author anty
 */
object NotificationsGroups {

    private val groups: MutableMap<String, NotificationGroup> = mutableMapOf()

    internal fun add(context: Context, group: NotificationGroup) {
        if (group.id in groups)
            throw IllegalArgumentException("Existing groupId: '${group.id}'")

        group.initialize(context)
        groups.put(group.id, group)
    }

    internal operator fun get(groupId: String): NotificationGroup {
        return groups[groupId]
                ?: throw IllegalArgumentException("Unknown groupId: '$groupId'")
    }

    internal fun getAll(): List<NotificationGroup> = groups.values.toList()
}
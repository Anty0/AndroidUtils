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
import eu.codetopic.utils.notifications.manager.util.NotificationChannel

/**
 * @author anty
 */
object NotificationsChannels {

    private val channels: MutableMap<String, NotificationChannel> = mutableMapOf()

    internal fun add(context: Context, channel: NotificationChannel) {
        if (channel.id in channels)
            throw IllegalArgumentException("Existing channelId: '${channel.id}'")

        refresh(context, channel)
        channels.put(channel.id, channel)
    }

    internal operator fun get(channelId: String): NotificationChannel {
        return channels[channelId]
                ?: throw IllegalArgumentException("Unknown channelId: '$channelId'")
    }

    internal fun getAll(): List<NotificationChannel> = channels.values.toList()

    internal fun refresh(context: Context, channelId: String) =
            refresh(context, get(channelId))

    internal fun refresh(context: Context, channel: NotificationChannel) =
            channel.initialize(context)
}
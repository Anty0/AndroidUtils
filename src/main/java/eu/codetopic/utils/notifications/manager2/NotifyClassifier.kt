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

package eu.codetopic.utils.notifications.manager2

import android.content.Context
import android.os.Build
import android.support.annotation.MainThread
import eu.codetopic.java.utils.debug.DebugAsserts.assert
import eu.codetopic.utils.AndroidExtensions.notificationManager
import eu.codetopic.utils.notifications.manager2.util.NotifyChannel
import eu.codetopic.utils.notifications.manager2.util.NotifyChannel.Companion.combinedIds
import eu.codetopic.utils.notifications.manager2.util.NotifyChannel.Companion.combinedIdFor
import eu.codetopic.utils.notifications.manager2.util.NotifyGroup

/**
 * @author anty
 */
@MainThread
internal object NotifyClassifier {

    private val REGEX_INVALID_OUT_OF_BRACKETS_CHARACTERS = Regex("[, ]")
    private val REGEX_BRACKETS = Regex("[()]")
    private val REGEX_IN_BRACKETS = Regex("\\(([^()]*?)\\)")
    private const val BRACKETS_VALID_PAIR = "()"

    private val GROUPS: MutableMap<String, NotifyGroup> = mutableMapOf()
    private val CHANNELS: MutableMap<String, NotifyChannel> = mutableMapOf()

    private fun String.isValidId(): Boolean {
        var brackets = REGEX_BRACKETS.findAll(this)
                .map { it.value }
                .joinToString("")

        while (BRACKETS_VALID_PAIR in brackets)
            brackets = brackets.replace(BRACKETS_VALID_PAIR, "")

        if (brackets.isNotEmpty()) return false

        var inBrackets = REGEX_IN_BRACKETS.replace(this, "")
        var inBracketsTmp = REGEX_IN_BRACKETS.replace(inBrackets, "")

        while (inBracketsTmp != inBrackets) {
            inBrackets = inBracketsTmp
            inBracketsTmp = REGEX_IN_BRACKETS.replace(inBrackets, "")
        }

        if (REGEX_INVALID_OUT_OF_BRACKETS_CHARACTERS.containsMatchIn(inBrackets)) return false

        return true
    }

    private fun NotifyGroup.install(context: Context) {
        id.assert { it.isValidId() }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        // installation is required only on Oreo+ devices

        if (NotifyManager.isOnNotifyManagerProcess(context)) {
            context.notificationManager.createNotificationChannelGroup(
                    createGroup(context).assert { it.id == id }
            )
        }
    }

    private fun NotifyChannel.install(context: Context) {
        id.assert { it.isValidId() }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        // installation is required only on Oreo+ devices

        if (NotifyManager.isOnNotifyManagerProcess(context)) {
            context.notificationManager.createNotificationChannels(
                    combinedIds().map { id ->
                        createChannel(context, id)
                                .assert { it.id == id }
                    }
            )
        }
    }

    private fun NotifyGroup.uninstall(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        // nothing to uninstall on versions bellow Oreo

        if (NotifyManager.isOnNotifyManagerProcess(context)) {
            context.notificationManager.also { nm ->
                findAllChannelsOf(this.id).forEach { channel ->
                    nm.deleteNotificationChannel(channel.combinedIdFor(this))
                }

                nm.deleteNotificationChannelGroup(id)
            }

            if (NotifyManager.isInitialized) NotifyManager.cleanup(context)
        }
    }

    private fun NotifyChannel.uninstall(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        // nothing to uninstall on versions bellow Oreo

        if (NotifyManager.isOnNotifyManagerProcess(context)) {
            context.notificationManager.also {
                combinedIds().forEach { id ->
                    it.deleteNotificationChannel(id)
                }
            }

            if (NotifyManager.isInitialized) NotifyManager.cleanup(context)
        }
    }

    fun validateGroupId(groupId: String): String =
            groupId.takeIf { it in GROUPS }
                    ?: throw IllegalArgumentException("Unknown groupId: '$groupId'")

    fun validateGroup(group: NotifyGroup): NotifyGroup =
            group.takeIf { it.id in GROUPS }
                    ?: throw IllegalArgumentException("Unknown group: '$group'")

    fun validateChannelId(channelId: String): String =
            channelId.takeIf { it in CHANNELS }
                    ?: throw IllegalArgumentException("Unknown channelId: '$channelId'")

    fun validateChannel(channel: NotifyChannel): NotifyChannel =
            channel.takeIf { it.id in CHANNELS }
                    ?: throw IllegalArgumentException("Unknown channel: '$channel'")

    fun install(context: Context, group: NotifyGroup) {
        if (hasGroup(group.id))
            throw IllegalArgumentException("Group with same id exist: '${group.id}'")

        GROUPS[group.id] = group

        // Create group
        group.install(context)

        // reinitialize group channels
        group.channelIds.forEach {
            // When channel doesn't exist, it's ok.
            // Because that channel will be probably added in future.
            CHANNELS[it]?.install(context)
        }
    }

    fun install(context: Context, channel: NotifyChannel) {
        if (hasChannel(channel.id))
            throw IllegalArgumentException("Channel with same id exist: '${channel.id}'")

        CHANNELS[channel.id] = channel

        // Create channels for all existing channel groups
        channel.install(context)
    }

    fun uninstallGroup(context: Context, groupId: String): NotifyGroup =
            findGroup(groupId).also {
                it.uninstall(context)
                GROUPS.remove(it.id)
            }

    fun uninstallChannel(context: Context, channelId: String) =
            findChannel(channelId).also {
                it.uninstall(context)
                CHANNELS.remove(it.id)
            }

    fun findGroup(groupId: String): NotifyGroup {
        return GROUPS[groupId]
                ?: throw IllegalArgumentException("Unknown groupId: '$groupId'")
    }

    fun findChannel(channelId: String): NotifyChannel {
        return CHANNELS[channelId]
                ?: throw IllegalArgumentException("Unknown channelId: '$channelId'")
    }

    fun findAllGroupsFor(channelId: String): List<NotifyGroup> =
            validateChannelId(channelId).let { id ->
                GROUPS.values.filter { id in it.channelIds }
            }

    fun findAllGroups(): List<NotifyGroup> = GROUPS.values.toList()

    fun findAllChannelsOf(groupId: String): List<NotifyChannel> =
            findGroup(groupId).channelIds.map { findChannel(it) }

    fun findAllChannels(): List<NotifyChannel> = CHANNELS.values.toList()

    fun hasGroup(groupId: String) = groupId in GROUPS

    fun hasChannel(channelId: String) = channelId in CHANNELS

    fun reinstallGroup(context: Context, groupId: String) =
            findGroup(groupId).install(context)

    fun reinstallChannel(context: Context, channelId: String) =
            findChannel(channelId).install(context)

    fun replace(context: Context, group: NotifyGroup) {
        validateGroup(group)

        group.install(context)
        GROUPS[group.id] = group
    }

    fun replace(context: Context, channel: NotifyChannel) {
        validateChannel(channel)

        channel.install(context)
        CHANNELS[channel.id] = channel
    }
}
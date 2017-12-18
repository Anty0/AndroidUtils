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

package eu.codetopic.utils.notifications.manager.save

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import eu.codetopic.java.utils.JavaExtensions.kSerializer
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.PrefNames.*
import eu.codetopic.utils.data.preferences.VersionedPreferencesData
import eu.codetopic.utils.data.preferences.preference.KotlinSerializedPreference
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider
import eu.codetopic.utils.data.preferences.support.PreferencesCompanionObject
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs
import eu.codetopic.utils.notifications.manager.NotificationsChannels
import eu.codetopic.utils.notifications.manager.NotificationsGroups
import eu.codetopic.utils.notifications.manager.data.CommonNotificationId
import eu.codetopic.utils.notifications.manager.data.NotificationInfo
import eu.codetopic.utils.notifications.manager.data.SummaryNotificationId
import eu.codetopic.utils.notifications.manager.util.NotificationChannel
import eu.codetopic.utils.notifications.manager.util.NotificationGroup
import kotlinx.serialization.list
import kotlinx.serialization.map

/**
 * @author anty
 */
class NotificationsData private constructor(context: Context) :
        VersionedPreferencesData<SharedPreferences>(context,
                BasicSharedPreferencesProvider(context, FILE_NAME_NOTIFICATIONS_DATA),
                SAVE_VERSION) {

    companion object : PreferencesCompanionObject<NotificationsData>(NotificationsData.LOG_TAG,
            ::NotificationsData, ::Getter) {

        private const val LOG_TAG = "NotificationsData"
        private const val SAVE_VERSION = 0
    }

    @Synchronized
    override fun onUpgrade(editor: SharedPreferences.Editor, from: Int, to: Int) {
        when (from) {
            -1 -> {
                // First start, nothing to do
            } // No more versions yet
        }
    }

    private var notificationsMap: Map<CommonNotificationId, NotificationInfo>
            by KotlinSerializedPreference<Map<CommonNotificationId, NotificationInfo>>(
                    NOTIFICATIONS_MAP,
                    (kSerializer<CommonNotificationId>() to kSerializer<NotificationInfo>()).map,
                    accessProvider,
                    { emptyMap() }
            )

    @Synchronized
    internal fun remove(id: CommonNotificationId): Bundle? {
        val map = notificationsMap.toMutableMap()
        return map.remove(id)?.data?.also { notificationsMap = map }
    }

    @Synchronized
    internal fun removeAll(ids: List<CommonNotificationId>): Map<CommonNotificationId, Bundle> {
        if (ids.isEmpty()) return emptyMap()

        val map = notificationsMap.toMutableMap()
        return ids.mapNotNull {
            id -> map.remove(id)?.data?.let { id to it }
        }.toMap().also {
            if (it.isNotEmpty()) notificationsMap = map
        }
    }

    @Synchronized
    internal fun removeAll(groupId: String? = null,
                           channelId: String? = null): Map<CommonNotificationId, Bundle> {
        val map = notificationsMap.toMutableMap()
        return map.filter {
            (groupId == null || it.key.groupId == groupId) &&
                    (channelId == null || it.key.channelId == channelId)
        }.map {
            it.also { map.remove(it.key) }.let { it.key to it.value.data }
        }.toMap().also {
            if (it.isNotEmpty()) notificationsMap = map
        }
    }

    @Synchronized
    internal fun removeAll(): Map<CommonNotificationId, Bundle> {
        val map = notificationsMap.toMutableMap()
        return map.map {
            it.key.also { map.remove(it) } to it.value.data
        }.toMap().also {
            if (it.isNotEmpty()) notificationsMap = map
        }
    }

    @Synchronized
    internal fun put(groupId: String, channelId: String, data: Bundle): CommonNotificationId =
            put(NotificationsGroups[groupId], NotificationsChannels[channelId], data)

    @Synchronized
    internal fun put(group: NotificationGroup, channel: NotificationChannel,
                     data: Bundle): CommonNotificationId {
        val id = CommonNotificationId(
                group.id, channel.id,
                group.nextId(context, channel, data)
        )
        val map = notificationsMap.toMutableMap()
        if (DebugMode.isEnabled && group.checkForIdOverrides && id in map) {
            Log.e(LOG_TAG, "add(groupId=${group.id}, channelId=${channel.id}, " +
                    "data=$data) -> (generatedId=${id.id}) -> " +
                    "Id generated for notification still exists in current context.")
        }
        map.put(id, NotificationInfo(data))
        notificationsMap = map
        return id
    }

    @Synchronized
    internal fun putAll(groupId: String, channelId: String, data: Bundle): CommonNotificationId =
            put(NotificationsGroups[groupId], NotificationsChannels[channelId], data)

    @Synchronized
    internal fun putAll(groupId: String, channelId: String,
                        data: List<Bundle>): Map<CommonNotificationId, Bundle> {
        val group = NotificationsGroups[groupId]
        val channel = NotificationsChannels[channelId]

        if (data.isEmpty()) return emptyMap()

        val map = notificationsMap.toMutableMap()
        val result = data.map {
            CommonNotificationId(
                    groupId, channelId,
                    group.nextId(context, channel, it)
            ).also { id ->
                if (DebugMode.isEnabled && group.checkForIdOverrides && id in map) {
                    Log.e(LOG_TAG, "addAll(groupId=$groupId, channelId=$channelId, " +
                            "data=$it) -> (generatedId=${id.id})" +
                            " -> Id generated for notification still exists in current context.")
                }
            } to it
        }
        map.putAll(result.map { it.first to NotificationInfo(it.second) })
        notificationsMap = map
        return result.toMap()
    }

    @Synchronized
    internal operator fun get(id: CommonNotificationId): Bundle? = notificationsMap[id]?.data

    @Synchronized
    internal fun getAll(): Map<CommonNotificationId, Bundle> = notificationsMap
            .map { it.key to it.value.data }.toMap()

    @Synchronized
    internal fun getAll(groupId: String? = null,
                        channelId: String? = null): Map<CommonNotificationId, Bundle> {
        return notificationsMap.filter {
            (groupId == null || it.key.groupId == groupId) &&
                    (channelId == null || it.key.channelId == channelId)
        }.map { it.key to it.value.data }.toMap()
    }

    @Synchronized
    internal operator fun contains(id: CommonNotificationId): Boolean = id in notificationsMap

    private class Getter : PreferencesGetterAbs<NotificationsData>() {

        override fun get() = instance

        override val dataClass: Class<out NotificationsData>
            get() = NotificationsData::class.java
    }
}
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

package eu.codetopic.utils.notifications.manager.save

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.MainThread
import eu.codetopic.java.utils.JavaExtensions.kSerializer
import eu.codetopic.java.utils.JavaExtensions.alsoIf
import eu.codetopic.java.utils.JavaExtensions.letIf
import eu.codetopic.java.utils.debug.DebugMode
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.PrefNames
import eu.codetopic.utils.bundle.BundleSerializer
import eu.codetopic.utils.data.preferences.PreferencesData
import eu.codetopic.utils.data.preferences.preference.KSerializedPreference
import eu.codetopic.utils.data.preferences.provider.ContentProviderPreferencesProvider
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences
import eu.codetopic.utils.data.preferences.support.PreferencesCompanionObject
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs
import eu.codetopic.utils.notifications.manager.NotifyClassifier
import eu.codetopic.utils.notifications.manager.NotifyManager
import eu.codetopic.utils.notifications.manager.data.CommonPersistentNotifyId
import eu.codetopic.utils.notifications.manager.data.NotifyId
import kotlinx.serialization.internal.BooleanSerializer
import kotlinx.serialization.internal.NullableSerializer
import kotlinx.serialization.internal.PairSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.list
import kotlinx.serialization.map

/**
 * @author anty
 */
@MainThread
internal class NotifyData private constructor(context: Context) :
        PreferencesData<ContentProviderSharedPreferences>(context,
                ContentProviderPreferencesProvider(context, NotifyProvider.AUTHORITY)) {

    companion object : PreferencesCompanionObject<NotifyData>(NotifyData.LOG_TAG,
            ::NotifyData, ::Getter) {

        private const val LOG_TAG = "NotifyData"
        internal const val SAVE_VERSION = 0

        @Suppress("UNUSED_PARAMETER")
        internal fun onUpgrade(editor: SharedPreferences.Editor, from: Int, to: Int) {
            // This function will be executed by provider in provider process
            when (from) {
                -1 -> {
                    // First start, nothing to do
                }
            } // No more versions yet
        }
    }

    private var notifyMapSave: Map<CommonPersistentNotifyId, Bundle>
            by KSerializedPreference<Map<CommonPersistentNotifyId, Bundle>>(
                    PrefNames.NOTIFICATIONS_MAP,
                    (kSerializer<CommonPersistentNotifyId>() to BundleSerializer).map,
                    accessProvider,
                    { emptyMap() }
            )

    private val notifyMapCache by lazy { notifyMapSave.toMutableMap() }

    private val notifyMap: MutableMap<CommonPersistentNotifyId, Bundle>
        get() =
            if (NotifyManager.isInitialized) notifyMapCache
            else notifyMapSave.toMutableMap()

    private fun notifyMapSave() {
        NotifyManager.assertInitialized(context)
        notifyMapSave = notifyMap
    }

    private var channelsEnableMapSave: Map<Pair<String, String>, Boolean>
            by KSerializedPreference<Map<Pair<String, String>, Boolean>>(
                    PrefNames.CHANNELS_ENABLE_MAP,
                    (PairSerializer(StringSerializer, StringSerializer) to BooleanSerializer).map,
                    accessProvider,
                    { emptyMap() }
            )

    private val channelsEnableMapCache by lazy { channelsEnableMapSave.toMutableMap() }

    private val channelsEnableMap: MutableMap<Pair<String, String>, Boolean>
        get() =
            if (NotifyManager.isInitialized) channelsEnableMapCache
            else channelsEnableMapSave.toMutableMap()

    private fun channelsEnableMapSave() {
        NotifyManager.assertInitialized(context)
        channelsEnableMapSave = channelsEnableMap
    }

    fun setChannelEnabled(groupId: String, channelId: String, enable: Boolean?) {
        NotifyManager.assertInitialized(context)
        val key = groupId to channelId
        if (enable == null) channelsEnableMap.remove(key)
        else channelsEnableMap[key] = enable
        channelsEnableMapSave()
    }

    fun isChannelEnabled(groupId: String, channelId: String): Boolean? =
            channelsEnableMap[groupId to channelId]

    fun remove(id: NotifyId): Bundle? {
        NotifyManager.assertInitialized(context)
        return id.let { it as? CommonPersistentNotifyId }?.let {
            notifyMap.remove(it)?.also { notifyMapSave() }
        }
    }

    fun removeAll(ids: Collection<NotifyId>): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        if (ids.isEmpty()) return emptyMap()

        return ids
                .mapNotNull { id ->
                    id.let { it as? CommonPersistentNotifyId }?.let { pId ->
                        notifyMap.remove(pId)?.let { pId to it }
                    }
                }
                .toMap<NotifyId, Bundle>()
                .alsoIf({ it.isNotEmpty() }) { notifyMapSave() }
    }

    fun removeAll(groupId: String? = null, channelId: String? = null): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)
        return notifyMap
                .letIf({ groupId != null }) { it.filter { it.key.idGroup == groupId } }
                .letIf({ channelId != null }) { it.filter { it.key.idChannel == channelId } }
                .onEach { notifyMap.remove(it.key) }
                .alsoIf({ it.isNotEmpty() }) { notifyMapSave() }
    }

    fun removeAll(): Map<out NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)
        return notifyMap.toMap()
                .onEach { notifyMap.remove(it.key) }
                .alsoIf({ it.isNotEmpty() }) { notifyMapSave() }
    }

    fun add(notifyId: NotifyId, data: Bundle) {
        NotifyManager.assertInitialized(context)

        if (notifyId !is CommonPersistentNotifyId) return
        // Ignore add requests of non persistent notifications

        if (DebugMode.isEnabled
                && NotifyClassifier.findChannel(notifyId.idChannel).checkForIdOverrides
                && notifyId in this) {
            Log.e(LOG_TAG, "add(notifyId=$notifyId, data=$data)" +
                    " -> Id generated for notification still exists in current context.")
        }

        notifyMap[notifyId] = data
        notifyMapSave()
    }

    fun addAll(map: Map<out NotifyId, Bundle>) {
        NotifyManager.assertInitialized(context)

        if (map.isEmpty()) return

        if (DebugMode.isEnabled) {
            map.forEach {
                val (notifyId, data) = it
                if (NotifyClassifier.findChannel(notifyId.idChannel).checkForIdOverrides
                        && notifyId in this) {
                    Log.e(LOG_TAG, "add(notifyId=$notifyId, data=$data)" +
                            " -> Id generated for notification still exists in current context.")
                }
            }
        }

        map.forEach {
            val (notifyId, data) = it
            if (notifyId is CommonPersistentNotifyId)
                notifyMap[notifyId] = data
        }
        notifyMapSave()
    }

    operator fun get(notifyId: NotifyId): Bundle? {
        if (notifyId !is CommonPersistentNotifyId) return null
        return notifyMap[notifyId]
    }

    fun getAll(): Map<out NotifyId, Bundle> =
            notifyMap.toMap()

    fun getAll(groupId: String? = null,
                        channelId: String? = null): Map<out NotifyId, Bundle> {
        return notifyMap
                .letIf({ groupId != null }) { it.filter { it.key.idGroup == groupId } }
                .letIf({ channelId != null }) { it.filter { it.key.idChannel == channelId } }
                .toMap()
    }

    operator fun contains(id: NotifyId): Boolean = id in notifyMap

    private class Getter : PreferencesGetterAbs<NotifyData>() {

        override fun get() = instance

        override val dataClass: Class<out NotifyData>
            get() = NotifyData::class.java
    }
}
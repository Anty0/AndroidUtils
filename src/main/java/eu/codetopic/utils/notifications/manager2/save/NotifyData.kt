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

package eu.codetopic.utils.notifications.manager2.save

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
import eu.codetopic.utils.PrefNames.FILE_NAME_NOTIFY_DATA
import eu.codetopic.utils.bundle.SerializableBundleWrapper
import eu.codetopic.utils.bundle.SerializableBundleWrapper.Companion.asSerializable
import eu.codetopic.utils.data.preferences.PreferencesData
import eu.codetopic.utils.data.preferences.VersionedPreferencesData
import eu.codetopic.utils.data.preferences.preference.KotlinSerializedPreference
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider
import eu.codetopic.utils.data.preferences.provider.ContentProviderPreferencesProvider
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences
import eu.codetopic.utils.data.preferences.support.PreferencesCompanionObject
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs
import eu.codetopic.utils.notifications.manager2.NotifyClassifier
import eu.codetopic.utils.notifications.manager2.NotifyManager
import eu.codetopic.utils.notifications.manager2.data.CommonPersistentNotifyId
import eu.codetopic.utils.notifications.manager2.data.NotifyId
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

    private var notifyMapSave: Map<CommonPersistentNotifyId, SerializableBundleWrapper>
            by KotlinSerializedPreference<Map<CommonPersistentNotifyId, SerializableBundleWrapper>>(
                    PrefNames.NOTIFICATIONS_MAP,
                    (kSerializer<CommonPersistentNotifyId>() to kSerializer<SerializableBundleWrapper>()).map,
                    accessProvider,
                    { emptyMap() }
            )

    private val notifyMapCache by lazy { notifyMapSave.toMutableMap() }

    private val notifyMap: MutableMap<CommonPersistentNotifyId, SerializableBundleWrapper>
        get() =
            if (NotifyManager.isInitialized) notifyMapCache
            else notifyMapSave.toMutableMap()

    private fun notifyMapSave() {
        NotifyManager.assertInitialized(context)
        notifyMapSave = notifyMap
    }

    fun remove(id: NotifyId): Bundle? {
        NotifyManager.assertInitialized(context)
        return id.let { it as? CommonPersistentNotifyId }?.let {
            notifyMap.remove(it)?.bundle?.also { notifyMapSave() }
        }
    }

    fun removeAll(ids: Collection<NotifyId>): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)

        if (ids.isEmpty()) return emptyMap()

        return ids.mapNotNull { id ->
            id.let { it as? CommonPersistentNotifyId }?.let { pId ->
                notifyMap.remove(pId)?.bundle?.let { pId to it }
            }
        }.toMap<NotifyId, Bundle>().alsoIf({ it.isNotEmpty() }) { notifyMapSave() }
    }

    fun removeAll(groupId: String? = null, channelId: String? = null): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)
        return notifyMap
                .letIf({ groupId != null }) { it.filter { it.key.idGroup == groupId } }
                .letIf({ channelId != null }) { it.filter { it.key.idChannel == channelId } }
                .onEach { notifyMap.remove(it.key) }
                .map { it.key to it.value.bundle }.toMap<NotifyId, Bundle>()
                .alsoIf({ it.isNotEmpty() }) { notifyMapSave() }
    }

    fun removeAll(): Map<NotifyId, Bundle> {
        NotifyManager.assertInitialized(context)
        return notifyMap.toMap()
                .onEach { notifyMap.remove(it.key) }
                .map { it.key to it.value.bundle }.toMap<NotifyId, Bundle>()
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

        notifyMap[notifyId] = data.asSerializable()
        notifyMapSave()
    }

    fun addAll(map: Map<NotifyId, Bundle>) {
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
                notifyMap[notifyId] = data.asSerializable()
        }
        notifyMapSave()
    }

    operator fun get(notifyId: NotifyId): Bundle? {
        if (notifyId !is CommonPersistentNotifyId) return null
        return notifyMap[notifyId]?.bundle
    }

    fun getAll(): Map<NotifyId, Bundle> =
            notifyMap.map { it.key to it.value.bundle }.toMap()

    fun getAll(groupId: String? = null,
                        channelId: String? = null): Map<NotifyId, Bundle> {
        return notifyMap
                .letIf({ groupId != null }) { it.filter { it.key.idGroup == groupId } }
                .letIf({ channelId != null }) { it.filter { it.key.idChannel == channelId } }
                .map { it.key to it.value.bundle }.toMap()
    }

    operator fun contains(id: NotifyId): Boolean = id in notifyMap

    private class Getter : PreferencesGetterAbs<NotifyData>() {

        override fun get() = instance

        override val dataClass: Class<out NotifyData>
            get() = NotifyData::class.java
    }
}
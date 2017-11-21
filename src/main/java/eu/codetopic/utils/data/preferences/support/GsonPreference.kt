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

package eu.codetopic.utils.data.preferences.support

import com.google.gson.Gson
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.edit
import java.lang.reflect.Type
import kotlin.reflect.KProperty

/**
 * @author anty
 */
class GsonPreference<T>(val key: String, private val gson: Gson, private val typeOfT: Type,
                        private val preferencesAccessor: ISharedPreferencesProvider<*>,
                        private val defaultValue: () -> T) {

    companion object {

        private val LOG_TAG = "GsonPreference"

        val DEFAULT_ID = "default"
    }

    private fun formatKey(id: String?): String {
        return "ID{${id ?: DEFAULT_ID}}-$key"
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getValue(thisRef, null)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>, id: String?): T {
        return getValue(thisRef, id)
    }

    fun getValue(syncObj: Any?, id: String? = null): T {
        synchronized(syncObj ?: this) {
            try {
                return gson.fromJson(
                        preferencesAccessor.preferences.getString(formatKey(id), null)
                                ?: return defaultValue(),
                        typeOfT)
            } catch (e: Exception) {
                Log.e(preferencesAccessor.name, "${formatKey(id)}: Failed to load", e)
                return defaultValue()
            }
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        setValue(thisRef, null, value)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, id: String?, value: T) {
        setValue(thisRef, id, value)
    }

    fun setValue(syncObj: Any?, id: String?, value: T) {
        synchronized(syncObj ?: this) {
            try {
                preferencesAccessor.preferences.edit {
                    putString(formatKey(id), gson.toJson(value))
                }
            } catch (e: Exception) {
                Log.e(preferencesAccessor.name, "${formatKey(id)}: Failed to save", e)
            }
        }
    }

    val isSet: Boolean @Synchronized get() = preferencesAccessor.preferences.contains(key)
}
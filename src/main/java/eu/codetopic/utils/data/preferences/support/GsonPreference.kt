/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
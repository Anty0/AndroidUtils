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

package eu.codetopic.utils.data.preferences.preference

import eu.codetopic.java.utils.log.Log
import kotlin.reflect.KProperty

/**
 * @author anty
 */
abstract class PreferenceAbs<T> {

    companion object {

        private const val LOG_TAG = "PreferenceAbs"

        const val DEFAULT_ID = "default"
    }

    abstract val name: String?
    abstract val key: String
    protected abstract val fallBackValue: T

    protected open fun formatKey(id: String?): String {
        return "ID{${id ?: DEFAULT_ID}}-$key"
    }

    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getValue(thisRef, null)
    }

    open operator fun getValue(thisRef: Any?, property: KProperty<*>, id: String?): T {
        return getValue(thisRef, id)
    }

    open fun getValue(syncObj: Any?, id: String? = null): T {
        return synchronized(syncObj ?: this) {
            try {
                getValue(formatKey(id))
            } catch (e: Exception) {
                Log.e(name ?: LOG_TAG, "${formatKey(id)}: Failed to load", e); fallBackValue
            }
        }
    }

    protected abstract fun getValue(key: String): T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        setValue(thisRef, null, value)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, id: String?, value: T) {
        setValue(thisRef, id, value)
    }

    fun setValue(syncObj: Any?, id: String?, value: T) {
        synchronized(syncObj ?: this) {
            try {
                setValue(formatKey(id), value)
            } catch (e: Exception) {
                Log.e(name ?: LOG_TAG, "${formatKey(id)}: Failed to save", e)
            }
        }
    }

    protected abstract fun setValue(key: String, value: T)

    open fun isSet(syncObj: Any?, id: String? = null): Boolean {
        return synchronized(syncObj ?: this) {
            try {
                isSet(formatKey(id))
            } catch (e: Exception) {
                Log.e(name ?: LOG_TAG, "${formatKey(id)}: Failed check if is set", e); false
            }
        }
    }

    protected abstract fun isSet(key: String): Boolean
}
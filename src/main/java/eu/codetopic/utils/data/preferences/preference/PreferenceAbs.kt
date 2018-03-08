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

        fun keyFor(id: String?, key: String): String =
                "ID{${id ?: DEFAULT_ID}}-$key"

        fun keyFor(key: String): String =
                keyFor(null, key)
    }

    abstract val name: String?
    abstract val key: String
    protected abstract val fallBackValue: T

    protected open fun formatKey(id: String?): String = keyFor(id, key)

    open operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get(thisRef, null)
    }

    open operator fun getValue(thisRef: Any?, property: KProperty<*>, id: String?): T {
        return get(thisRef, id)
    }

    open operator fun get(syncObj: Any?, id: String? = null): T {
        return synchronized(syncObj ?: this) {
            try {
                get(formatKey(id))
            } catch (e: Exception) {
                Log.e(name ?: LOG_TAG, "${formatKey(id)}: Failed to load", e); fallBackValue
            }
        }
    }

    protected abstract fun get(key: String): T

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(thisRef, null, value)
    }

    open operator fun setValue(thisRef: Any?, property: KProperty<*>, id: String?, value: T) {
        set(thisRef, id, value)
    }

    open operator fun set(syncObj: Any?, id: String?, value: T) {
        synchronized(syncObj ?: this) {
            try {
                set(formatKey(id), value)
            } catch (e: Exception) {
                Log.e(name ?: LOG_TAG, "${formatKey(id)}: Failed to save", e)
            }
        }
    }

    protected abstract fun set(key: String, value: T)

    open fun unset(syncObj: Any?, id: String?) {
        synchronized(syncObj ?: this) {
            try {
                unset(formatKey(id))
            } catch (e: Exception) {
                Log.e(name ?: LOG_TAG, "${formatKey(id)}: Failed to save", e)
            }
        }
    }

    protected abstract fun unset(key: String)

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
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

import android.content.SharedPreferences
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JSON

/**
 * @author anty
 */
open class KSerializedPreference<T : Any>(override val key: String,
                                          private val serializer: KSerializer<T>,
                                          provider: ISharedPreferencesProvider<*>,
                                          private val defaultValue: () -> T) :
        BasePreference<T, SharedPreferences>(provider) {

    constructor(key: String, serializer: KSerializer<T>,
                provider: ISharedPreferencesProvider<*>,
                defaultValue: T) :
            this(key, serializer, provider, { defaultValue })

    override val fallBackValue: T get() = defaultValue()

    override fun SharedPreferences.getValue(key: String): T {
        return getString(key, null)?.let {
            JSON.parse(serializer, it)
        } ?: defaultValue()
    }

    override fun SharedPreferences.Editor.putValue(key: String, value: T) {
        putString(key, JSON.stringify(serializer, value))
    }
}
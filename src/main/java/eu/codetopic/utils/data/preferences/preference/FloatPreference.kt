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

/**
 * @author anty
 */
class FloatPreference(override val key: String,
                      provider: ISharedPreferencesProvider<*>,
                      private val defaultValue: Float) :
        BasePreference<Float, SharedPreferences>(provider) {

    override val fallBackValue: Float get() = defaultValue

    override fun SharedPreferences.getValue(key: String): Float = getFloat(key, defaultValue)

    override fun SharedPreferences.Editor.putValue(key: String, value: Float) { putFloat(key, value) }
}
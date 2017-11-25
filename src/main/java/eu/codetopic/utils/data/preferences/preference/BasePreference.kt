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
import eu.codetopic.utils.AndroidExtensions.edit

/**
 * @author anty
 */
abstract class BasePreference<T, out SP : SharedPreferences>(
        protected val provider: ISharedPreferencesProvider<SP>) : PreferenceAbs<T>() {

    override val name: String? get() = provider.name

    open val preferences: SP get() = provider.preferences

    override fun getValue(key: String): T = preferences.getValue(key)

    protected abstract fun SharedPreferences.getValue(key: String): T

    override fun setValue(key: String, value: T) = preferences.edit { putValue(key, value) }

    protected abstract fun SharedPreferences.Editor.putValue(key: String, value: T)

    override fun isSet(key: String): Boolean = preferences.contains(key)
}
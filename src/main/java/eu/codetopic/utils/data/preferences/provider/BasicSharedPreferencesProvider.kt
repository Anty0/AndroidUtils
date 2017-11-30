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

package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class BasicSharedPreferencesProvider(context: Context, private val fileName: String? = null,
                                     preferencesOperatingMode: Int = Context.MODE_PRIVATE) :
        ISharedPreferencesProvider<SharedPreferences> {

    val preferencesOperatingMode = if (fileName == null) Context.MODE_PRIVATE else preferencesOperatingMode

    private fun createPreferences(context: Context, preferencesOperatingMode: Int): SharedPreferences {
        return if (fileName == null) PreferenceManager.getDefaultSharedPreferences(context)
        else context.getSharedPreferences(fileName, preferencesOperatingMode)
    }

    override val name: String? by lazy { "BasicSharedPreferences.{${fileName ?: "default"}}" }

    override val preferences: SharedPreferences by lazy { createPreferences(context.applicationContext, preferencesOperatingMode) }

    override fun toString(): String =
            "BasicSharedPreferencesProvider(" +
                    "fileName=$fileName, " +
                    "preferences=$preferences, " +
                    "preferencesOperatingMode=$preferencesOperatingMode)"
}
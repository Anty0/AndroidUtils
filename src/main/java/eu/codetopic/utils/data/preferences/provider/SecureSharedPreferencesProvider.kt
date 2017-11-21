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

import android.content.SharedPreferences
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.data.preferences.support.SecurePreferences

class SecureSharedPreferencesProvider<out SP : SharedPreferences>(
        private val basePreferencesProvider: ISharedPreferencesProvider<SP>,
        password: String = DEFAULT_PASSWORD) :
        ISharedPreferencesProvider<SecurePreferences<SP>> {

    companion object {

        private const val LOG_TAG = "SecureSharedPreferencesProvider"
        const val DEFAULT_PASSWORD = "Copyright 2017 Jiří Kuchyňka (Anty)"
    }

    private fun createPreferences(password: String): SecurePreferences<SP> {
        try {
            return SecurePreferences(basePreferencesProvider, password)
        } catch (t: Throwable) {
            Log.e(LOG_TAG, "createPreferences: failed to create SecurePreferences", t)
            throw t
        }
    }

    override val name: String? by lazy { "SecureSharedPreferences.${basePreferencesProvider.name}" }

    override val preferences: SecurePreferences<SP> by lazy { createPreferences(password) }

    override fun toString(): String =
            "SecureSharedPreferencesProvider(basePreferencesProvider='$basePreferencesProvider', preferences=$preferences)"
}
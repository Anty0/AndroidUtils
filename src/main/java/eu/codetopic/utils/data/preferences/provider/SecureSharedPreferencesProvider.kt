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
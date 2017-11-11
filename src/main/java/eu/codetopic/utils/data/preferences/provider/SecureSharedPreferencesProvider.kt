/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.preferences.provider

import android.content.Context
import com.securepreferences.SecurePreferences
import eu.codetopic.java.utils.log.Log

class SecureSharedPreferencesProvider(context: Context, private val fileName: String,
                                      password: String = DEFAULT_PASSWORD,
                                      clearOnFail: Boolean = false) :
        ISharedPreferencesProvider<SecurePreferences> {

    companion object {
        private const val LOG_TAG = "SecureSharedPreferencesProvider"
        const val DEFAULT_PASSWORD = "TheBestDefaultPasswordEver"
    }

    private val preferences = createPreferences(context.applicationContext, password, clearOnFail)


    private fun createPreferences(context: Context, password: String, clearOnFail: Boolean): SecurePreferences {
        try {
            return SecurePreferences(context, password, fileName)
        } catch (t: Throwable) {
            if (clearOnFail) {
                Log.e(LOG_TAG, "createPreferences: failed to create SecurePreferences, preferences will be cleared", t)
                val pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
                if (pref.all.isEmpty()) {
                    Log.e(LOG_TAG, "createPreferences: failed to clear preferences, preferences are empty", t)
                    throw t
                }
                pref.edit().clear().apply()
                return createPreferences(context, password, clearOnFail)
            }
            Log.e(LOG_TAG, "createPreferences: failed to create SecurePreferences", t)
            throw t
        }
    }

    override fun getName(): String? = "SecureSharedPreferences.$1%s".format(fileName)

    override fun getSharedPreferences(): SecurePreferences = preferences

    override fun toString(): String =
            "SecureSharedPreferencesProvider(fileName='$fileName', preferences=$preferences)"
}
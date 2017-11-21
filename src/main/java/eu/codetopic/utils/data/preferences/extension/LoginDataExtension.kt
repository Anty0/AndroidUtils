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

package eu.codetopic.utils.data.preferences.extension

import android.content.SharedPreferences

import eu.codetopic.java.utils.log.Log

import eu.codetopic.utils.PrefNames.LOGGED_IN
import eu.codetopic.utils.PrefNames.PASSWORD
import eu.codetopic.utils.PrefNames.USERNAME
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.edit

open class LoginDataExtension<out SP : SharedPreferences>(private val provider: ISharedPreferencesProvider<SP>) {

    open val isLoggedIn: Boolean
        @Synchronized get() = isLoggedIn(null)

    open val username: String?
        @Synchronized get() = getUsername(null)

    open val password: String?
        @Synchronized get() = getPassword(null)

    companion object {

        private const val LOG_TAG = "LoginDataExtension"

        const val DEFAULT_ID = "default"
    }

    private fun log(methodName: String, id: String?) {
        Log.d(LOG_TAG, "$methodName: { name: '${provider.name}', id: '${id ?: DEFAULT_ID}' }")
    }

    protected open fun formatKey(id: String?, key: String): String {
        return "ID{${id ?: DEFAULT_ID}}-$key"
    }

    @Synchronized
    open fun login(username: String, password: String) {
        login(null, username, password)
    }

    @Synchronized
    open fun login(id: String?, username: String, password: String) {
        log("login", id)
        provider.preferences.edit {
            putString(formatKey(id, USERNAME), username)
            putString(formatKey(id, PASSWORD), password)
            putBoolean(formatKey(id, LOGGED_IN), true)
        }
    }

    @Synchronized
    open fun logout() {
        logout(null)
    }

    @Synchronized
    open fun logout(id: String?) {
        log("logout", id)
        provider.preferences.edit {
            putBoolean(formatKey(id, LOGGED_IN), false)
            remove(formatKey(id, PASSWORD))
        }
    }

    @Synchronized
    open fun isLoggedIn(id: String?): Boolean {
        log("isLoggedIn", id)
        return provider.preferences.getBoolean(formatKey(id, LOGGED_IN), false)
    }

    @Synchronized
    open fun getUsername(id: String?): String? {
        log("getUsername", id)
        return provider.preferences.getString(USERNAME, null)
    }

    @Synchronized
    open fun getPassword(id: String?): String? {
        log("getPassword", id)
        return provider.preferences.getString(PASSWORD, null)
    }


    @Synchronized
    open fun clearData() {
        clearData(null)
    }

    @Synchronized
    open fun clearData(id: String?) {
        log("clearData", id)
        provider.preferences.edit {
            remove(formatKey(id, USERNAME))
            remove(formatKey(id, PASSWORD))
            remove(formatKey(id, LOGGED_IN))
        }
    }

    @Synchronized
    open fun changeId(id: String?, newId: String?) {
        log("changeId", "$id->$newId")
        if (id == newId) return

        val prefs = provider.preferences
        provider.preferences.edit {
            putString(formatKey(newId, USERNAME), prefs.getString(formatKey(id, USERNAME), null))
            putString(formatKey(newId, PASSWORD), prefs.getString(formatKey(id, PASSWORD), null))
            putBoolean(formatKey(newId, LOGGED_IN), prefs.getBoolean(formatKey(id, LOGGED_IN), false))
            remove(formatKey(id, USERNAME))
            remove(formatKey(id, PASSWORD))
            remove(formatKey(id, LOGGED_IN))
        }
    }
}

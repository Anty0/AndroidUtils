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

package eu.codetopic.utils.data.preferences.extension

import android.content.SharedPreferences

import eu.codetopic.java.utils.log.Log

import eu.codetopic.utils.PrefNames.LOGGED_IN
import eu.codetopic.utils.PrefNames.PASSWORD
import eu.codetopic.utils.PrefNames.USERNAME
import eu.codetopic.utils.data.preferences.support.IPreferencesAccessor

open class LoginDataExtension<out SP : SharedPreferences>(private val accessor: IPreferencesAccessor<SP>) {

    open val isLoggedIn: Boolean
        @Synchronized get() = isLoggedIn(null)

    open val username: String?
        @Synchronized get() = getUsername(null)

    open val password: String?
        @Synchronized get() = getPassword(null)

    companion object {

        private val LOG_TAG = "LoginDataExtension"

        val DEFAULT_ID = "default"
    }

    private fun log(methodName: String, id: String?) {
        Log.d(LOG_TAG, "$methodName: { name: '${accessor.name}', id: '${id ?: DEFAULT_ID}' }")
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
        accessor.edit {
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
        accessor.edit {
            putBoolean(formatKey(id, LOGGED_IN), false)
            remove(formatKey(id, PASSWORD))
        }
    }

    @Synchronized
    open fun isLoggedIn(id: String?): Boolean {
        log("isLoggedIn", id)
        return accessor.preferences.getBoolean(formatKey(id, LOGGED_IN), false)
    }

    @Synchronized
    open fun getUsername(id: String?): String? {
        log("getUsername", id)
        return accessor.preferences.getString(USERNAME, null)
    }

    @Synchronized
    open fun getPassword(id: String?): String? {
        log("getPassword", id)
        return accessor.preferences.getString(PASSWORD, null)
    }


    @Synchronized
    open fun clearData() {
        clearData(null)
    }

    @Synchronized
    open fun clearData(id: String?) {
        log("clearData", id)
        accessor.edit {
            remove(formatKey(id, USERNAME))
            remove(formatKey(id, PASSWORD))
            remove(formatKey(id, LOGGED_IN))
        }
    }

    @Synchronized
    open fun changeId(id: String?, newId: String?) {
        log("changeId", "$id->$newId")
        if (id == newId) return

        val prefs = accessor.preferences
        accessor.edit {
            putString(formatKey(newId, USERNAME), prefs.getString(formatKey(id, USERNAME), null))
            putString(formatKey(newId, PASSWORD), prefs.getString(formatKey(id, PASSWORD), null))
            putBoolean(formatKey(newId, LOGGED_IN), prefs.getBoolean(formatKey(id, LOGGED_IN), false))
            remove(formatKey(id, USERNAME))
            remove(formatKey(id, PASSWORD))
            remove(formatKey(id, LOGGED_IN))
        }
    }
}

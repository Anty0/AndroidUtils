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

package eu.codetopic.utils.data.preferences.extension

import android.content.SharedPreferences
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider

/**
 * @author anty
 */
open class LoginDataWithNullIdExtension<out SP : SharedPreferences>(
        provider: ISharedPreferencesProvider<SP>): LoginDataExtension<SP>(provider) {

    companion object {

        private const val LOG_TAG = "LoginDataWithNullIdExtension"

        const val DEFAULT_ID = "default"
    }

    open val isLoggedIn: Boolean
        @Synchronized get() = isLoggedIn(DEFAULT_ID)

    open val username: String?
        @Synchronized get() = getUsername(DEFAULT_ID)

    open val password: String?
        @Synchronized get() = getPassword(DEFAULT_ID)

    @Synchronized
    open fun login(username: String, password: String) {
        login(DEFAULT_ID, username, password)
    }

    @Synchronized
    open fun logout() {
        logout(DEFAULT_ID)
    }

    @Synchronized
    open fun clearData() {
        clearData(DEFAULT_ID)
    }
}
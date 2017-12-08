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
import com.google.gson.Gson
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import java.lang.reflect.Type

/**
 * @author anty
 */
open class CachedGsonPreference<T>(key: String, gson: Gson, typeOfT: Type,
                              provider: ISharedPreferencesProvider<*>, defaultValue: () -> T) :
        GsonPreference<T>(key, gson, typeOfT, provider, defaultValue) { // FIXME: find way to clone value stored in cache, so cache will stay immutable.

    constructor(key: String, gson: Gson, typeOfT: Type, provider: ISharedPreferencesProvider<*>,
                defaultValue: T) : this(key, gson, typeOfT, provider, { defaultValue })

    protected var cachedValue: T? = null

    protected fun SharedPreferences.getFreshValue(key: String): T {
        return gson.fromJson(getString(key, null)
                ?: return fallBackValue, typeOfT)
    }

    override fun SharedPreferences.getValue(key: String): T {
        return cachedValue ?: run {
            val value = getFreshValue(key)
            cachedValue = value
            value
        }
    }

    override fun SharedPreferences.Editor.putValue(key: String, value: T) {
        cachedValue = value
        putString(key, gson.toJson(value, typeOfT))
    }
}
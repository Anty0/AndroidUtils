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

package eu.codetopic.utils.ids

import android.content.Context
import android.content.SharedPreferences

import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.data.preferences.PreferencesData
import eu.codetopic.utils.data.preferences.VersionedPreferencesData
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider

import eu.codetopic.utils.PrefNames.ADD_LAST_ID
import eu.codetopic.utils.PrefNames.FILE_NAME_IDENTIFIERS
import eu.codetopic.utils.PrefNames.ID_TYPE_NOTIFICATION_ID
import eu.codetopic.utils.PrefNames.ID_TYPE_REQUEST_CODE
import eu.codetopic.utils.data.preferences.provider.ContentProviderPreferencesProvider
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences
import eu.codetopic.utils.data.preferences.support.PreferencesCompanionObject
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs
import java.io.Serializable

class Identifiers private constructor(context: Context) : PreferencesData<ContentProviderSharedPreferences>(context,
        ContentProviderPreferencesProvider(context, IdentifiersProvider.AUTHORITY)) {

    companion object : PreferencesCompanionObject<Identifiers>(Identifiers.LOG_TAG, ::Identifiers, ::Getter) {

        val TYPE_REQUEST_CODE = Type(ID_TYPE_REQUEST_CODE)
        val TYPE_NOTIFICATION_ID = Type(ID_TYPE_NOTIFICATION_ID)

        private const val LOG_TAG = "Identifiers"
        internal const val SAVE_VERSION = 0

        internal fun onUpgrade(editor: SharedPreferences.Editor, from: Int, to: Int) {
            // This function will be executed by provider in provider process
            when (from) {
                -1 -> {
                    // First start, nothing to do
                }
            } // No more versions yet
        }

        fun next(type: Type): Int = instance.getNext(type)
    }

    private fun getLast(type: Type) = preferences.getInt(type.settingsName, type.min)

    private fun getNext(type: Type): Int {
        val next = type.getNext(getLast(type))
        edit { putInt(type.settingsName, next) }
        Log.d(LOG_TAG, "Returning next identifier of type "
                + type.name + ", returned id is " + next)
        return next
    }

    private class Getter : PreferencesGetterAbs<Identifiers>() {

        override fun get() = instance

        override val dataClass: Class<out Identifiers>
            get() = Identifiers::class.java
    }

    class Type(val name: String, val min: Int = 1, val max: Int = Integer.MAX_VALUE): Serializable {

        val settingsName: String get() = ADD_LAST_ID + name

        init {
            if (min >= max) throw IllegalArgumentException("Min cannot be same of higher then Max. " + this)
        }

        fun getNext(last: Int) = if (last >= max - 1) min else last + 1

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Type

            if (name != other.name) return false
            if (min != other.min) return false
            if (max != other.max) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + min
            result = 31 * result + max
            return result
        }

        override fun toString(): String {
            return "Type(name='$name', min=$min, max=$max)"
        }
    }

}

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
import eu.codetopic.utils.PrefNames.*
import eu.codetopic.utils.data.preferences.PreferencesData
import eu.codetopic.utils.data.preferences.VersionedPreferencesData
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider

import eu.codetopic.utils.data.preferences.preference.IntPreference
import eu.codetopic.utils.data.preferences.preference.StringPreference
import eu.codetopic.utils.data.preferences.provider.ContentProviderPreferencesProvider
import eu.codetopic.utils.data.preferences.support.ContentProviderSharedPreferences
import eu.codetopic.utils.data.preferences.support.PreferencesCompanionObject
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs
import java.io.Serializable
import kotlin.coroutines.experimental.buildSequence

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

        fun sequenceOf(type: Type): Sequence<Int> = buildSequence {
            while (true) yield(next(type))
        }

        fun Type.nextId(): Int = next(this)

        fun Type.asIdsSequence(): Sequence<Int> = sequenceOf(this)
    }

    private val lastPref = StringPreference(LAST_IDENTIFIER, accessProvider, "")

    private fun Type.getLastValue() = lastPref.getValue(this, name)
            .takeIf { it.isNotEmpty() }?.toIntOrNull() ?: min

    private fun Type.setLastValue(value: Int) = lastPref.setValue(this, name, value.toString())

    private fun Type.getNextValue() = getNext(getLastValue()).also { setLastValue(it) }

    @Synchronized
    private fun getNext(type: Type): Int = type.getNextValue().also {
        Log.d(LOG_TAG, "getNext(type=$type) -> (nextIdentifier=$it)")
    }

    private class Getter : PreferencesGetterAbs<Identifiers>() {

        override fun get() = instance

        override val dataClass: Class<out Identifiers>
            get() = Identifiers::class.java
    }

    class Type(val name: String, val min: Int = 1, val max: Int = Integer.MAX_VALUE): Serializable {

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

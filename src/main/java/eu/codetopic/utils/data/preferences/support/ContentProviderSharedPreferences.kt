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

package eu.codetopic.utils.data.preferences.support

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareQueryOrDeleteUri
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareInsertOrUpdateUri
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.Segment
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareAllKeysQueryUri
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareUriBase
import java.util.*

class ContentProviderSharedPreferences private constructor(
        private val context: Context, private val authority: String) :
        SharedPreferences {

    companion object {

        private val CONTENT = Any()

        private val instancesMap = mutableMapOf<String, ContentProviderSharedPreferences>()

        fun getInstance(context: Context, authority: String): ContentProviderSharedPreferences {
            return instancesMap[authority] ?:
                    ContentProviderSharedPreferences(context.applicationContext, authority)
                            .also { instancesMap[authority] = it }
        }
    }

    private val lock = Any()
    private val changeListeners = WeakHashMap<SharedPreferences.OnSharedPreferenceChangeListener, Any>()

    private val changesObserver = object : ContentObserver(Handler(context.mainLooper)) {
        override fun onChange(selfChange: Boolean) {
            notifyChanged()
        }
    }.also { context.contentResolver.registerContentObserver(prepareUriBase(authority), true, it) }

    val name get() = findValueByKey(query(prepareQueryOrDeleteUri(authority, Segment.CONTROL, Segment.CONTROL_VALUE_NAME)), Segment.CONTROL_VALUE_NAME) ?: "unknown"

    private fun query(uri: Uri): Cursor {
        return context.contentResolver.query(uri, null, null, null, null)
    }

    private fun findValueByKey(cursor: Cursor, key: String): String? {
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            if (cursor.getString(0) == key)
                return cursor.getString(1)
            cursor.moveToNext()
        }
        return null
    }

    private fun get(key: String): String? {
        return findValueByKey(query(prepareQueryOrDeleteUri(authority, Segment.DATA, key)), key)
    }

    private fun notifyChanged() {
        synchronized(lock) {
            changeListeners.forEach { it.key.onSharedPreferenceChanged(this, null) }
        }
    }

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        synchronized(lock) {
            changeListeners.put(listener, CONTENT)
        }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        synchronized(lock) {
            changeListeners.remove(listener)
        }
    }

    override fun contains(key: String): Boolean {
        return findValueByKey(query(prepareQueryOrDeleteUri(authority, Segment.CONTAINS, key)), key)?.toBoolean() ?: false
    }

    override fun getAll(): MutableMap<String, *> {
        return mutableMapOf(*query(prepareAllKeysQueryUri(authority, Segment.DATA)).let {
            val pairs = mutableListOf<Pair<String, String?>>()
            it.moveToFirst()
            while (!it.isAfterLast) {
                pairs.add(it.getString(0) to it.getString(1))
                it.moveToNext()
            }
            return@let pairs.toTypedArray()
        })
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return get(key)?.toBoolean() ?: defValue
    }

    override fun getInt(key: String, defValue: Int): Int {
        return get(key)?.toInt() ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        return get(key)?.toLong() ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return get(key)?.toFloat() ?: defValue
    }

    override fun getString(key: String, defValue: String?): String? {
        return get(key) ?: defValue
    }

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? {
        TODO("not implemented")
    }

    inner class EditorImpl : SharedPreferences.Editor {

        private var changeClear = false
        private val changesMap = mutableMapOf<String, String?>()

        private fun insert(uri: Uri): Boolean {
            return context.contentResolver.insert(uri, changesMap.let {
                val values = ContentValues(it.size)
                changesMap.forEach { values.put(it.key, it.value) }
                return@let values
            }) != null
        }

        private fun push(): Boolean {
            return insert(prepareInsertOrUpdateUri(authority, Segment.DATA, changeClear)).also {
                changeClear = false
                changesMap.clear()
            }
        }

        override fun commit(): Boolean {
            return push()
        }

        override fun apply() {
            push()
        }

        override fun clear(): SharedPreferences.Editor {
            changesMap.clear()
            changeClear = true
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            changesMap[key] = null
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            changesMap[key] = value.toString()
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            changesMap[key] = value.toString()
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            changesMap[key] = value.toString()
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            changesMap[key] = value.toString()
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            changesMap[key] = value
            return this
        }

        override fun putStringSet(key: String, values: MutableSet<String>?): SharedPreferences.Editor {
            TODO("not implemented")
        }
    }
}
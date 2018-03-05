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
import android.os.Build
import android.os.Handler
import eu.codetopic.java.utils.alsoIfNull
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.Column
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.Query
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.Segment
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareAllKeysQueryUri
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareInsertOrUpdateUri
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareQueryOrDeleteUri
import eu.codetopic.utils.data.preferences.support.ContentProviderPreferences.Companion.prepareUriSegmentBase
import java.util.*

class ContentProviderSharedPreferences private constructor(
        private val context: Context, private val authority: String) :
        SharedPreferences {

    companion object {

        private const val LOG_TAG = "ContentProviderSharedPreferences"

        private val CONTENT = Any()

        private val instancesMap = mutableMapOf<String, ContentProviderSharedPreferences>()

        private inline fun <T : Cursor?, R> T.use(block: (T) -> R): R {
            // Cursor cannot be casted to Closeable on low api, so we must use own function
            //  instead of one included in kotlin for closing Cursors, to avoid casting problems.

            var exception: Throwable? = null
            try {
                return block(this)
            } catch (e: Throwable) {
                exception = e
                throw e
            } finally {
                closeFinally(exception)
            }
        }

        private fun Cursor?.closeFinally(cause: Throwable?) {
            when {
                this == null -> Unit
                cause == null -> close()
                else ->
                    try {
                        close()
                    } catch (closeException: Throwable) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            cause.addSuppressed(closeException)
                        }
                    }
            }
        }

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
            notifyChanged(null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            val key = uri?.getQueryParameter(Query.KEY)
            notifyChanged(key)
        }
    }

    /*val name
        get() = query(prepareQueryOrDeleteUri(authority, Segment.CONTROL, Segment.CONTROL_VALUE_NAME))
                ?.use { findValueByKey(it, Segment.CONTROL_VALUE_NAME) } ?: "unknown"*/

    init {
        context.contentResolver.registerContentObserver(prepareUriSegmentBase(authority, Segment.DATA),
                false, changesObserver)
    }

    private fun query(uri: Uri): Cursor? {
        return context.contentResolver.query(uri, null,
                null, null, null)
                ?.alsoIfNull {
                    Log.e(LOG_TAG, "query(uri=$uri)",
                            NullPointerException("Received null cursor from query request"))
                }
    }

    private fun findValueByKey(cursor: Cursor, key: String): String? {
        if (!cursor.moveToFirst()) return null
        val columnName = cursor.getColumnIndex(Column.NAME)
        val columnValue = cursor.getColumnIndex(Column.VALUE)
        while (!cursor.isAfterLast) {
            if (cursor.getString(columnName) == key)
                return cursor.getString(columnValue)
            cursor.moveToNext()
        }
        return null
    }

    private fun get(key: String): String? {
        return query(prepareQueryOrDeleteUri(authority, Segment.DATA, key))
                ?.use { findValueByKey(it, key) }
    }

    private fun notifyChanged(key: String?) {
        synchronized(lock) {
            changeListeners.forEach { it.key.onSharedPreferenceChanged(this, key) }
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
        return query(prepareQueryOrDeleteUri(authority, Segment.CONTAINS, key))
                ?.use { findValueByKey(it, key)?.toBoolean() } ?: false
    }

    override fun getAll(): MutableMap<String, *> {
        return mutableMapOf(
                *query(prepareAllKeysQueryUri(authority, Segment.DATA))
                        ?.use {
                            val pairs = mutableListOf<Pair<String, String?>>()
                            it.moveToFirst()
                            while (!it.isAfterLast) {
                                pairs.add(it.getString(0) to it.getString(1))
                                it.moveToNext()
                            }
                            return@use pairs.toTypedArray()
                        }
                        ?: emptyArray()
        )
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
                ContentValues(it.size).apply { it.forEach { put(it.key, it.value) } }
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
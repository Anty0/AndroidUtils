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

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.database.Cursor
import android.net.Uri
import android.support.annotation.CallSuper
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.cursor.MultiColumnCursor
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.edit

abstract class ContentProviderPreferences<out SP : SharedPreferences>(
        val authority: String
) : ContentProvider() {

    companion object {

        private const val LOG_TAG: String = "ContentProviderPreferences"

        private fun buildUriBase(authority: String): Uri.Builder {
            return Uri.Builder()
                    .scheme("content")
                    .authority(authority)
        }

        private fun buildUriSegmentBase(authority: String, segment: String): Uri.Builder {
            return buildUriBase(authority)
                    .appendPath(segment)
        }

        fun prepareUriBase(authority: String): Uri {
            return buildUriBase(authority).build()
        }

        fun prepareUriSegmentBase(authority: String, segment: String): Uri {
            return buildUriSegmentBase(authority, segment)
                    .build()
        }

        fun prepareAllKeysQueryUri(authority: String, segment: String): Uri {
            return buildUriSegmentBase(authority, segment)
                    .appendQueryParameter(Query.ALL_KEYS, "true")
                    .build()
        }

        fun prepareQueryOrDeleteUri(authority: String, segment: String, vararg keys: String): Uri {
            return buildUriSegmentBase(authority, segment)
                    .apply { keys.forEach { appendQueryParameter(Query.KEY, it) } }
                    .build()
        }

        fun prepareInsertOrUpdateUri(authority: String, segment: String, clearData: Boolean): Uri {
            return buildUriSegmentBase(authority, segment)
                    .appendQueryParameter(Query.CLEAR, if (clearData) "true" else "false")
                    .build()
        }

        object Segment {
            const val CONTROL = "control"
            //const val CONTROL_VALUE_NAME = "name"

            const val DATA = "data"

            const val CONTAINS = "contains"
        }

        object Query {
            const val KEY = "key"
            const val ALL_KEYS = "allKeys"
            const val CLEAR = "clear"
        }

        object Column {
            const val NAME = "name"
            const val VALUE = "value"
        }
    }

    private lateinit var preferencesProvider: ISharedPreferencesProvider<SP>

    //protected val name: String? get() = preferencesProvider.name

    protected val preferences: SP get() = preferencesProvider.preferences

    private val preferencesChangeListener = OnSharedPreferenceChangeListener { _, key ->
        Log.v(LOG_TAG, "onSharedPreferenceChange(key=$key)")
        onChange(key)
    }

    @CallSuper
    override fun onCreate(): Boolean {
        Log.v(LOG_TAG, "onCreate()")
        preferencesProvider = onPreparePreferencesProvider()
        preferences.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
        return true
    }

    @CallSuper
    override fun shutdown() {
        Log.v(LOG_TAG, "shutdown()")
        preferences.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.shutdown()
    }

    abstract fun onPreparePreferencesProvider(): ISharedPreferencesProvider<SP>

    @CallSuper
    protected open fun onChange(key: String) {
        val uri: Uri = buildUriSegmentBase(authority, Segment.DATA)
                .appendQueryParameter(Query.KEY, key).build()
        context.contentResolver.notifyChange(uri, null)
    }

    override fun getType(uri: Uri): String? = null

    private fun cursorOf(valuesMap: Map<String, Any?>) : Cursor {
        val entries = valuesMap.entries.toList()
        return MultiColumnCursor(
                Array(entries.size, { i ->
                    with(entries[i]) {
                        arrayOf(key, value)
                    }
                }),
                arrayOf(Column.NAME, Column.VALUE)
        )
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        Log.v(LOG_TAG, "query(uri=$uri)")
        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return null

        when (path[0]) {
            Segment.CONTROL -> {
                if (uri.getBooleanQueryParameter(Query.ALL_KEYS, false)) {
                    return cursorOf(mapOf(
                            //Segment.CONTROL_VALUE_NAME to name
                    ))
                }

                return cursorOf(uri.getQueryParameters(Query.KEY).map {
                    /*when (it) {
                        Segment.CONTROL_VALUE_NAME -> {
                            Segment.CONTROL_VALUE_NAME to name
                        }
                        else -> it to null
                    }*/
                    it to null
                }.toMap())
            }
            Segment.DATA -> {
                if (uri.getBooleanQueryParameter(Query.ALL_KEYS, false)) {
                    return cursorOf(preferences.all)
                }

                return cursorOf(uri.getQueryParameters(Query.KEY).map {
                    it to preferences.getString(it, null)
                }.toMap())
            }
            Segment.CONTAINS -> {
                if (uri.getBooleanQueryParameter(Query.ALL_KEYS, false)) {
                    return cursorOf(preferences.all.keys.map {
                        it to true
                    }.toMap())
                }

                return cursorOf(uri.getQueryParameters(Query.KEY).map {
                    it to preferences.contains(it)
                }.toMap())
            }
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.v(LOG_TAG, "insert(uri=$uri)")
        if (values == null) return null

        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return null

        return when (path[0]) {
            Segment.CONTROL -> null // Control values cannot be edited
            Segment.DATA -> {
                preferences.edit {
                    if (uri.getBooleanQueryParameter(Query.CLEAR, false)) {
                        clear()
                    }

                    values.keySet().forEach {
                        putString(it, values.getAsString(it))
                    }
                }

                //context.contentResolver.notifyChange(uri, null)

                prepareQueryOrDeleteUri(authority, Segment.DATA,
                        *values.keySet().toTypedArray())
            }
            Segment.CONTAINS -> null // Contains cannot be used to edit values
            else -> null
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.v(LOG_TAG, "delete(uri=$uri)")
        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return 0

        return when (path[0]) {
            Segment.CONTROL -> 0 // Control values cannot be edited
            Segment.DATA -> {
                val keys = uri.getQueryParameters(Query.KEY)
                preferences.edit {
                    keys.forEach {
                        remove(it)
                    }
                }

                //context.contentResolver.notifyChange(uri, null)
                keys.size
            }
            Segment.CONTAINS -> 0 // Contains cannot be used to edit values
            else -> 0
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        Log.v(LOG_TAG, "delete(uri=$uri)")
        if (values == null) return 0

        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return 0

        return when (path[0]) {
            Segment.CONTROL -> 0 // Control values cannot be edited
            Segment.DATA -> {
                preferences.edit {
                    if (uri.getBooleanQueryParameter(Query.CLEAR, false)) {
                        clear()
                    }

                    values.keySet().forEach {
                        putString(it, values.getAsString(it))
                    }
                }

                //context.contentResolver.notifyChange(uri, null)
                values.size()
            }
            Segment.CONTAINS -> 0 // Contains cannot be used to edit values
            else -> 0
        }
    }
}
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

package eu.codetopic.utils.data.preferences.support

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.support.annotation.CallSuper
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.simple.SimpleMultiColumnCursor

abstract class ContentProviderPreferences<out SP : SharedPreferences>(
        private val authority: String) : ContentProvider() {

    private lateinit var preferencesProvider: ISharedPreferencesProvider<SP>

    protected val name: String? get() = preferencesProvider.getName()

    protected val preferences: SP get() = preferencesProvider.getSharedPreferences()

    protected fun edit(block: SharedPreferences.Editor.() -> Unit) =
            preferences.edit().apply { block() }.apply()

    companion object {

        fun prepareUriBase(authority: String): Uri {
            return Uri.Builder()
                    .scheme("content")
                    .authority(authority)
                    .build()
        }

        fun prepareAllKeysQueryUri(authority: String, segment: String): Uri {
            return Uri.Builder()
                    .scheme("content")
                    .authority(authority)
                    .appendPath(segment)
                    .appendQueryParameter(Query.ALL_KEYS, "true")
                    .build()
        }

        fun prepareQueryOrDeleteUri(authority: String, segment: String, vararg keys: String): Uri {
            return Uri.Builder()
                    .scheme("content")
                    .authority(authority)
                    .appendPath(segment)
                    .apply { keys.forEach { appendQueryParameter(Query.KEY, it) } }
                    .build()
        }

        fun prepareInsertOrUpdateUri(authority: String, segment: String, clearData: Boolean): Uri {
            return Uri.Builder()
                    .scheme("content")
                    .authority(authority)
                    .appendPath(segment)
                    .appendQueryParameter(Query.CLEAR, if (clearData) "true" else "false")
                    .build()
        }

        object Segment {
            const val CONTROL = "control"
            const val CONTROL_VALUE_NAME = "name"

            const val DATA = "data"

            const val CONTAINS = "contains"
        }

        object Query {
            const val KEY = "key"
            const val ALL_KEYS = "allKeys"
            const val CLEAR = "clear"
        }
    }

    @CallSuper
    override fun onCreate(): Boolean {
        preferencesProvider = onPreparePreferencesProvider()
        return true
    }

    abstract fun onPreparePreferencesProvider(): ISharedPreferencesProvider<SP>

    override fun getType(uri: Uri): String? {
        return null
    }

    private fun cursorOf(valuesMap: Map<String, Any?>) : Cursor {
        val entries = valuesMap.entries.toList()
        return SimpleMultiColumnCursor(
                Array(entries.size, { i ->
                    with (entries[i]) {
                        arrayOf(key, value)
                    }
                }),
                arrayOf("name", "value")
        )
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return null

        when (path[0]) {
            Segment.CONTROL -> {
                if (uri.getBooleanQueryParameter(Query.ALL_KEYS, false)) {
                    return cursorOf(mapOf(
                            Segment.CONTROL_VALUE_NAME to preferencesProvider.getName()
                    ))
                }

                return cursorOf(uri.getQueryParameters(Query.KEY).map {
                    when (it) {
                        Segment.CONTROL_VALUE_NAME -> {
                            Segment.CONTROL_VALUE_NAME to preferencesProvider.getName()
                        }
                        else -> it to null
                    }
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
        if (values == null) return null

        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return null

        when (path[0]) {
            Segment.CONTROL -> return null // Control values cannot be edited
            Segment.DATA -> {
                edit {
                    if (uri.getBooleanQueryParameter(Query.CLEAR, false)) {
                        clear()
                    }

                    values.keySet().forEach {
                        putString(it, values.getAsString(it))
                    }
                }

                context.contentResolver.notifyChange(uri, null)
                return prepareQueryOrDeleteUri(authority, Segment.DATA,
                        *values.keySet().toTypedArray())
            }
            Segment.CONTAINS -> return null // Contains cannot be used to edit values
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return 0

        when (path[0]) {
            Segment.CONTROL -> return 0 // Control values cannot be edited
            Segment.DATA -> {
                val keys = uri.getQueryParameters(Query.KEY)
                edit {
                    keys.forEach {
                        remove(it)
                    }
                }

                context.contentResolver.notifyChange(uri, null)
                return keys.size
            }
            Segment.CONTAINS -> return 0 // Contains cannot be used to edit values
        }
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        if (values == null) return 0

        val path = uri.pathSegments.takeIf { it.size == 1 } ?: return 0

        when (path[0]) {
            Segment.CONTROL -> return 0 // Control values cannot be edited
            Segment.DATA -> {
                edit {
                    if (uri.getBooleanQueryParameter(Query.CLEAR, false)) {
                        clear()
                    }

                    values.keySet().forEach {
                        putString(it, values.getAsString(it))
                    }
                }

                context.contentResolver.notifyChange(uri, null)
                return values.size()
            }
            Segment.CONTAINS -> return 0 // Contains cannot be used to edit values
        }
        return 0
    }
}
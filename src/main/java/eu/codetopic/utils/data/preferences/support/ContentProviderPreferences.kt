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

package eu.codetopic.utils.data.preferences.support

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.support.annotation.CallSuper
import eu.codetopic.utils.data.preferences.provider.ISharedPreferencesProvider
import eu.codetopic.utils.edit
import eu.codetopic.utils.simple.SimpleMultiColumnCursor

abstract class ContentProviderPreferences<out SP : SharedPreferences>(
        private val authority: String) : ContentProvider() {

    private lateinit var preferencesProvider: ISharedPreferencesProvider<SP>

    protected val name: String? get() = preferencesProvider.name

    protected val preferences: SP get() = preferencesProvider.preferences

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
                            Segment.CONTROL_VALUE_NAME to name
                    ))
                }

                return cursorOf(uri.getQueryParameters(Query.KEY).map {
                    when (it) {
                        Segment.CONTROL_VALUE_NAME -> {
                            Segment.CONTROL_VALUE_NAME to name
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

                context.contentResolver.notifyChange(uri, null)

                prepareQueryOrDeleteUri(authority, Segment.DATA,
                        *values.keySet().toTypedArray())
            }
            Segment.CONTAINS -> null // Contains cannot be used to edit values
            else -> null
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
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

                context.contentResolver.notifyChange(uri, null)
                keys.size
            }
            Segment.CONTAINS -> 0 // Contains cannot be used to edit values
            else -> 0
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
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

                context.contentResolver.notifyChange(uri, null)
                values.size()
            }
            Segment.CONTAINS -> 0 // Contains cannot be used to edit values
            else -> 0
        }
    }
}
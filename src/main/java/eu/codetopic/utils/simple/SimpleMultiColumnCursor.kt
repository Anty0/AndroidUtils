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

package eu.codetopic.utils.simple

import android.database.AbstractCursor
import android.database.CursorIndexOutOfBoundsException

class SimpleMultiColumnCursor(private val data: Array<Array<Any?>>, private val columnNames: Array<String>) : AbstractCursor() {

    private operator fun get(column: Int): Any? {
        checkPosition()
        val row = data[position]
        if (column < 0 || column >= row.size) {
            throw CursorIndexOutOfBoundsException(column, row.size)
        }
        return row[column]
    }

    override fun getLong(column: Int): Long {
        val value = this[column]
        return when (value) {
            is Long -> value
            is String -> value.toLong()
            else -> value as Long
        }
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getColumnNames(): Array<String> {
        return columnNames
    }

    override fun getShort(column: Int): Short {
        val value = this[column]
        return when (value) {
            is Short -> value
            is String -> value.toShort()
            else -> value as Short
        }
    }

    override fun getFloat(column: Int): Float {
        val value = this[column]
        return when (value) {
            is Float -> value
            is String -> value.toFloat()
            else -> value as Float
        }
    }

    override fun getDouble(column: Int): Double {
        val value = this[column]
        return when (value) {
            is Double -> value
            is String -> value.toDouble()
            else -> value as Double
        }
    }

    override fun isNull(column: Int): Boolean {
        return this[column] == null
    }

    override fun getInt(column: Int): Int {
        val value = this[column]
        return when (value) {
            is Int -> value
            is String -> value.toInt()
            else -> value as Int
        }
    }

    override fun getString(column: Int): String {
        val value = this[column]
        return when (value) {
            is String -> value
            else -> value as String
        }
    }
}
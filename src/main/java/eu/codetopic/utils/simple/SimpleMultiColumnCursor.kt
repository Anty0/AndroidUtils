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
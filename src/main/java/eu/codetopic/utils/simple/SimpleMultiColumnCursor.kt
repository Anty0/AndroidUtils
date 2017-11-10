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
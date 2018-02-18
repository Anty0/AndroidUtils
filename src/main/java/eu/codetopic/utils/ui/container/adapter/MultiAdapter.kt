/*
 * utils
 * Copyright (C)   2018  anty
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

package eu.codetopic.utils.ui.container.adapter

import android.content.Context
import android.support.annotation.UiThread
import eu.codetopic.utils.AndroidExtensions.edit
import eu.codetopic.utils.ui.container.items.custom.CustomItem

/**
 * @author anty
 */
class MultiAdapter<T : CustomItem>(
        context: Context,
        private val comparator: Comparator<in T>
) : CustomItemAdapter<T>(context) {

    companion object {

        private const val LOG_TAG = "MultiAdapter"
        private val EDIT_TAG = Any()
    }

    private val map: MutableMap<String, MutableList<T>> = mutableMapOf()

    private fun getId(id: String): MutableList<T> = map.getOrPut(id, ::mutableListOf)

    @UiThread
    fun mapAdd(id: String, item: T) {
        try {
            getId(id).add(item)
        } finally {
            mapUpdate()
        }
    }

    @UiThread
    fun mapAddAll(id: String, items: List<T>) {
        try {
            getId(id).addAll(items)
        } finally {
            mapUpdate()
        }
    }

    @UiThread
    fun mapReplaceAll(id: String, items: Collection<T>) {
        try {
            getId(id).apply {
                clear()
                addAll(items)
            }
        } finally {
            mapUpdate()
        }
    }

    @UiThread
    fun mapRemoveAll(id: String) {
        try {
            getId(id).clear()
        } finally {
            mapUpdate()
        }
    }

    @UiThread
    fun mapGetAll(id: String): List<T> {
        try {
            return getId(id).toList()
        } finally {
            mapUpdate()
        }
    }

    @UiThread
    fun mapUpdate() = edit {
        clear()
        mutableListOf<T>()
                .apply {
                    map.values.forEach { addAll(it) }
                    sortWith(comparator)
                }
                .also { addAll(it) }
       tag = EDIT_TAG
    }

    override fun assertAllowApplyChanges(editTag: Any?,
                                         modifications: Collection<Modification<T>>,
                                         contentModifiedItems: Collection<T>?) {
        super.assertAllowApplyChanges(editTag, modifications, contentModifiedItems)
        if (EDIT_TAG !== editTag)
            throw UnsupportedOperationException("$LOG_TAG can't be externally edited, " +
                    "use method push() to push changes")
    }
}
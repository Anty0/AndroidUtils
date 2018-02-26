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

package eu.codetopic.utils.ui.container.adapter

import android.support.annotation.UiThread

import java.lang.ref.WeakReference
import java.util.Collections

import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.ui.container.adapter.ArrayEditAdapter.CalculatingMode.*
import eu.codetopic.java.utils.forEachIterate
import eu.codetopic.java.utils.debug.DebugMode

abstract class ArrayEditAdapter<E : Any, VH : UniversalHolder<*>>() :
        UniversalAdapter<VH>(), Iterable<E> {

    companion object {

        private const val LOG_TAG = "ArrayEditAdapter"
    }

    private val dataLock = Any()
    private val data = mutableListOf<E>()

    override val itemCount: Int
        get() = synchronized(dataLock) { data.size }

    open val items: List<E>
        get() = synchronized(dataLock) { data.toList() }

    override val isEmpty: Boolean
        get() = synchronized(dataLock) { data.isEmpty() }

    constructor(data: Collection<E>) : this() {
        this.data.addAll(data)
    }

    constructor(vararg data: E) : this() {
        Collections.addAll(this.data, *data)
    }

    override fun getItem(position: Int): E = synchronized(dataLock) { data[position] }

    open fun getItemPosition(item: E): Int = synchronized(dataLock) { data.indexOf(item) }

    override fun iterator(): Iterator<E> = object : Iterator<E> by items.iterator() {}

    fun edit(): Editor<E> = Editor(this)

    protected open fun assertAllowApplyChanges(editTag: Any?,
                                               modifications: Collection<Modification<E>>,
                                               contentModifiedItems: Collection<E>?) {
    }

    @UiThread
    open fun postModifications(mode: CalculatingMode,
                               modifications: Collection<Modification<E>>,
                               contentModifiedItems: Collection<E>?) {

        postModifications(null, mode, modifications, contentModifiedItems)
    }

    @UiThread
    open fun postModifications(editTag: Any?, mode: CalculatingMode,
                               modifications: Collection<Modification<E>>,
                               contentModifiedItems: Collection<E>?) {

        assertAllowApplyChanges(editTag, modifications, contentModifiedItems)

        synchronized(dataLock) {
            if (!isBaseAttached) return modifications.forEach { it(data, null) }

            val realMode = mode.takeIf { !base.hasOnlySimpleDataChangedReporting() } ?: NO_ANIMATIONS

            try {
                when (realMode) {
                    NO_ANIMATIONS -> {
                        modifications.forEach { it(data, null) }
                        base.notifyDataSetChanged()
                        return
                    }
                    FROM_MODIFICATIONS -> {
                        val base = base
                        modifications.forEach { it(data, base) }
                    }
                    EQUALS_DETECTION -> {
                        val dataBackup = data.toMutableList()

                        modifications.forEach { it(data, null) }

                        val oldEmpty = dataBackup.isEmpty()
                        val newEmpty = data.isEmpty()
                        when {
                            oldEmpty && newEmpty -> {
                            }
                            oldEmpty -> base.notifyItemRangeInserted(0, data.size)
                            newEmpty -> base.notifyItemRangeRemoved(0, dataBackup.size)
                            else -> {
                                // Cache base, don't get it every time when some change is found
                                val base = base

                                dataBackup.forEachIterate { iterator, obj ->
                                    if (!data.contains(obj)) {
                                        base.notifyItemRemoved(dataBackup.indexOf(obj))
                                        iterator.remove()
                                    }
                                }

                                data.forEachIndexed forI@ { i, obj ->
                                    if (!dataBackup.contains(obj)) {
                                        dataBackup.add(i, obj)
                                        base.notifyItemInserted(i)
                                        return@forI
                                    }

                                    val oldIndex = dataBackup.indexOf(obj)
                                    if (oldIndex != i) {
                                        base.notifyItemMoved(oldIndex, i)
                                        dataBackup.remove(obj)
                                        dataBackup.add(i, obj)
                                    }
                                }

                                if (DebugMode.isEnabled && dataBackup != data)
                                    Log.e(LOG_TAG, "apply", InternalError("Detected problem " +
                                            "in $LOG_TAG while applying changes (dataBackup != newData) ->" +
                                            "\n(dataBackup=$dataBackup,\nnewData=$data"))
                            }
                        }
                    }
                    // else -> throw IllegalArgumentException("Unknown mode -> (mode=$mode)")
                }

                if (realMode != NO_ANIMATIONS) {
                    if (contentModifiedItems != null) {
                        val base = base // Cache base, don't get it every iteration
                        contentModifiedItems.mapNotNull {
                            data.indexOf(it).takeIf { it != -1 }
                        }.forEach { base.notifyItemChanged(it) }
                    } else base.notifyItemRangeChanged(0, data.size)
                }
            } finally {
                onDataEdited(editTag)
            }
        }
    }

    protected open fun onDataEdited(editTag: Any?) {

    }

    enum class CalculatingMode {
        NO_ANIMATIONS, EQUALS_DETECTION, FROM_MODIFICATIONS
    }

    class Editor<E : Any>(adapter: ArrayEditAdapter<E, *>) {

        companion object {

            private const val LOG_TAG = "${ArrayEditAdapter.LOG_TAG}\$Editor"
        }

        private val adapterReference: WeakReference<out ArrayEditAdapter<E, *>> = WeakReference(adapter)
        private val modifications = mutableListOf<Modification<E>>()
        private val changedItems = mutableListOf<E>()
        private var allItemsChanged = false

        @get:Synchronized
        @set:Synchronized
        var tag: Any? = null

        @Synchronized
        fun post(modification: Modification<E>) {
            modifications.add(modification)
        }

        fun add(obj: E) = post {
            add(obj)
            it?.notifyItemInserted(size - 1)
        }

        fun add(index: Int, obj: E) = post {
            add(index, obj)
            it?.notifyItemInserted(index)
        }

        fun <AT : E> addAll(array: Array<AT>) = addAll(array.asList())

        fun addAll(collection: Collection<E>) = post {
            addAll(collection)
            it?.apply {
                val count = collection.size
                notifyItemRangeInserted(size - count, count)
            }
        }

        fun addAll(index: Int, collection: Collection<E>) = post {
            addAll(index, collection)
            it?.notifyItemRangeInserted(index, collection.size)
        }

        fun clear() = post {
            if (it == null) {
                clear()
                return@post
            }

            val count = size
            clear()
            it.notifyItemRangeRemoved(0, count)
        }

        fun removeAt(index: Int) = post {
            removeAt(index)
            it?.notifyItemRemoved(index)
        }

        fun remove(obj: Any) = post {
            if (it == null) {
                remove(obj)
                return@post
            }

            indexOf(obj).takeIf { it != -1 }?.apply {
                removeAt(this)
                it.notifyItemRemoved(this)
            }
        }

        operator fun set(index: Int, obj: E) = post {
            set(index, obj)
            it?.notifyItemChanged(index)
        }

        fun removeAll(collection: Collection<*>) = post {
            if (it == null) {
                removeAll(collection)
                return@post
            }

            collection.mapNotNull {
                indexOf(it).takeIf { it != -1 }
            }.forEach { index ->
                removeAt(index)
                it.notifyItemRemoved(index)
            }
        }

        fun retainAll(collection: Collection<*>) = post {
            if (it == null) {
                retainAll(collection)
                return@post
            }

            forEachIterate { iterator, obj ->
                if (!collection.contains(obj)) {
                    it.notifyItemRemoved(indexOf(obj))
                    iterator.remove()
                }
            }
        }

        @Synchronized
        fun notifyItemsChanged(vararg items: E) {
            Collections.addAll(changedItems, *items)
        }

        @Synchronized
        fun notifyItemsChanged(items: Collection<E>) {
            changedItems.addAll(items)
        }

        @Synchronized
        fun notifyAllItemsChanged(){
            allItemsChanged = true
        }

        @Synchronized
        fun apply(): Boolean = apply(CalculatingMode.EQUALS_DETECTION)

        @UiThread
        @Synchronized
        fun apply(mode: CalculatingMode): Boolean {
            return adapterReference.get()?.let {
                it.postModifications(tag, mode, modifications,
                        if (allItemsChanged) null else changedItems)
                modifications.clear()
                changedItems.clear()
                allItemsChanged = false

                true
            } ?: false
        }
    }
}

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

import android.content.Context
import eu.codetopic.java.utils.log.Log
import eu.codetopic.java.utils.simple.SimpleSuspendLock
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.items.custom.EmptyCustomItem
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.asReference

abstract class AutoLoadAdapter(context: Context) : CustomItemAdapter<CustomItem>(context) {

    companion object {

        private const val LOG_TAG = "AutoLoadAdapter"
        private val EDIT_TAG = Any()
    }

    private val suspendLock = SimpleSuspendLock()

    private var page: Int? = null
    var nextPage: Int
        private set(value) { page = value }
        get() = page ?: run { startingPage.also { page = it } }
    protected open val startingPage: Int
        get() = 0

    var isEnabled = true
        set(enabled) {
            if (field != enabled) {
                field = enabled
                if (field) reset()
                else showLoadingItem = false
            }
        }

    private var showLoadingItem = true
        set(show) {
            if (field != show) {
                field = show
                if (show) base.notifyItemInserted(super.itemCount)
                else base.notifyItemRemoved(super.itemCount)
            }
        }
    var loadingItem: CustomItem = EmptyCustomItem()
        set(value) {
            field = value
            if (isBaseAttached && showLoadingItem)
                base.notifyItemChanged(super.itemCount)
        }

    override val itemCount: Int
        get() = super.itemCount.let { if (showLoadingItem) it + 1 else it }

    override val items: List<CustomItem>
        get() = throw UnsupportedOperationException("Not supported")

    override val isEmpty: Boolean
        get() = !showLoadingItem && super.isEmpty

    override fun onBindViewHolder(holder: UniversalViewHolder, position: Int) {
        try {
            super.onBindViewHolder(holder, position)
        } finally {
            if (showLoadingItem && position >= super.itemCount - 1) // last item or loading item
                loadNextPage(false)
        }
    }

    fun loadNextPage(force: Boolean): Job? {
        when {
            suspendLock.tryLock() -> {
                return doLoadNextPage().also {
                    it.invokeOnCompletion(onCancelling = true) {
                        suspendLock.unlock()
                    }
                }
            }
            force -> {
                return launch {
                    suspendLock.suspendLock()
                    doLoadNextPage().also {
                        it.invokeOnCompletion(onCancelling = true) {
                            suspendLock.unlock()
                        }
                    }.join()
                }
            }
            else -> Log.d(LOG_TAG, "loadNextPage(force=$force) -> Still loading, " +
                    "skipping next page load.")
        }
        return null
    }

    private fun doLoadNextPage(): Job {
        val page: Int = nextPage++
        val firstPage = page == startingPage

        val editor = edit()
        editor.takeIf { firstPage }?.apply {
            clear()
            notifyAllItemsChanged()
        }

        val self = this.asReference()
        val nextPageLoader = onLoadNextPage(page, editor)
        return launch(UI) {
            val result = nextPageLoader.await()
            editor.tag = EDIT_TAG
            editor.apply()

            self().apply {
                showLoadingItem = result
                if (firstPage && isBaseAttached) {
                    // first page auto scroll down fix
                    base.takeIf { !it.hasOnlySimpleDataChangedReporting() }
                            ?.notifyDataSetChanged()
                }
            }
        }
    }

    protected abstract fun onLoadNextPage(page: Int, editor: ArrayEditAdapter.Editor<CustomItem>): Deferred<Boolean>

    override fun onAttachToContainer(container: Any?) {
        if (page == null && super.isEmpty) loadNextPage(false)
        super.onAttachToContainer(container)
    }

    fun reset(): Job? {
        page = null
        return loadNextPage(true)
    }

    override fun getItem(position: Int): CustomItem =
            if (position == super.itemCount) loadingItem
            else super.getItem(position)

    override fun getItemPosition(item: CustomItem): Int {
        return if (item == loadingItem) super.itemCount else super.getItemPosition(item)
    }

    override fun assertAllowApplyChanges(editTag: Any?,
                                         modifications: Collection<Modification<CustomItem>>,
                                         contentModifiedItems: Collection<CustomItem>?) {
        super.assertAllowApplyChanges(editTag, modifications, contentModifiedItems)
        if (EDIT_TAG !== editTag)
            throw UnsupportedOperationException("$LOG_TAG can't be edited anytime, " +
                    "you must override method onLoadNextPage() and apply your changes here")
    }

    override fun onDataEdited(editTag: Any?) {
        super.onDataEdited(editTag)
        showLoadingItem = isEnabled
    }
}

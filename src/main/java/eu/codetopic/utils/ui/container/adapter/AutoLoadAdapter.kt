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
import android.support.v7.widget.RecyclerView
import java.util.concurrent.locks.ReentrantLock

import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.R
import eu.codetopic.utils.thread.LooperUtils
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper
import eu.codetopic.utils.ui.container.items.custom.LoadingItem
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.asReference

abstract class AutoLoadAdapter(context: Context) : CustomItemAdapter<CustomItem>(context) {

    companion object {

        private const val LOG_TAG = "AutoLoadAdapter"
        private val EDIT_TAG = Any()
    }

    private val suspendLock = ReentrantLock()

    private var page: Int? = null
    var nextPage: Int
        private set(value) { page = value }
        get() = page ?: run { startingPage.apply { page = this } }
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
    private val loadingItem by lazy(::generateLoadingItem)

    override val itemCount: Int
        get() = super.itemCount.let { if (showLoadingItem) it + 1 else it }

    override val items: List<CustomItem>
        get() = throw UnsupportedOperationException("Not supported")

    override val isEmpty: Boolean
        get() = !showLoadingItem && super.isEmpty

    protected open fun generateLoadingItem(): CustomItem {
        val useCardView = base is RecyclerView.Adapter<*>
        return object : LoadingItem(context, R.string.wait_text_loading) {
            override fun getWrappers(context: Context): Array<CustomItemWrapper> {
                return if (useCardView) CardViewWrapper.WRAPPER else emptyArray()
            }
        }
    }

    override fun onBindViewHolder(holder: UniversalAdapter.ViewHolder, position: Int) {
        try {
            super.onBindViewHolder(holder, position)
        } finally {
            if (showLoadingItem && position == super.itemCount)
                loadNextPage(false)
        }
    }

    fun loadNextPage(force: Boolean) {
        when {
            suspendLock.tryLock() -> {
                val page: Int = nextPage++
                val firstPage = page == startingPage

                val editor = edit()
                editor.takeIf { firstPage }?.apply {
                    clear()
                    notifyAllItemsChanged()
                }

                val adapterRef = this.asReference()
                val nextPageLoader = onLoadNextPage(page, editor)
                launch(UI) {
                    val result = nextPageLoader.await()
                    editor.tag = EDIT_TAG
                    editor.apply()

                    adapterRef().apply {
                        showLoadingItem = result
                        if (firstPage) base.takeIf { !it.hasOnlySimpleDataChangedReporting() }
                                ?.notifyDataSetChanged() // first page auto scroll down fix
                        suspendLock.unlock()
                    }
                }
            }
            force -> {
                Log.d(LOG_TAG, "loadNextPage(force=$force) -> Still loading, " +
                        "trying to wait one more looper loop.")
                LooperUtils.postOnContextThread(context) {
                    loadNextPage(true)
                }
            }
            else -> Log.d(LOG_TAG, "loadNextPage(force=$force) -> Still loading, " +
                    "skipping next page load.")
        }
    }

    protected abstract fun onLoadNextPage(page: Int, editor: ArrayEditAdapter.Editor<CustomItem>): Deferred<Boolean>

    override fun onAttachToContainer(container: Any?) {
        if (page == null && super.isEmpty) loadNextPage(false)
        super.onAttachToContainer(container)
    }

    fun reset() {
        page = null
        loadNextPage(true)
    }

    override fun getItem(position: Int): CustomItem {
        return if (position == super.itemCount) loadingItem else super.getItem(position)
    }

    override fun getItemPosition(item: CustomItem): Int {
        return if (item == loadingItem) super.itemCount else super.getItemPosition(item)
    }

    override fun getItems(contents: Array<CustomItem>): Array<CustomItem> {
        throw UnsupportedOperationException("Not supported")
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

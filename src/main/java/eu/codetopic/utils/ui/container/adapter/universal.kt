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

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.SpinnerAdapter
import eu.codetopic.utils.getOrPut
import eu.codetopic.utils.ui.view.getTag
import eu.codetopic.utils.ui.view.setTag

abstract class UniversalAdapter<VH : UniversalHolder<*>> {

    companion object {

        private const val LOG_TAG = "UniversalAdapter"

        const val NO_VIEW_TYPE = 0
    }

    private var _base: UniversalAdapterBase = EmptyUniversalAdapterBase
    var base: UniversalAdapterBase
        @JvmName("attachBase") set(value) {
            if (_base != EmptyUniversalAdapterBase) throw IllegalStateException("$LOG_TAG's base is still attached")
            _base = value
            onBaseAttached(value)
        }
        get() {
            return if (_base == EmptyUniversalAdapterBase)
                throw IllegalStateException("$LOG_TAG's base is not attached")
            else _base
        }

    val isBaseAttached: Boolean
        get() = _base !== EmptyUniversalAdapterBase

    abstract val itemCount: Int

    open val isEmpty: Boolean
        get() = itemCount == 0

    protected open fun onBaseAttached(base: UniversalAdapterBase) {}

    abstract fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH

    abstract fun onBindViewHolder(holder: VH, position: Int)

    abstract fun getItem(position: Int): Any

    open fun getItemId(position: Int): Long = position.toLong()

    open fun hasStableIds(): Boolean = false

    @LayoutRes
    open fun getItemViewType(position: Int): Int = NO_VIEW_TYPE

    open fun onDataSetChanged() {}

    open fun onAttachToContainer(container: Any?) {}

    open fun onDetachFromContainer(container: Any?) {}

    open fun onBeforeRegisterDataObserver(observer: Any) {}

    open fun onAfterRegisterDataObserver(observer: Any) {}

    open fun onBeforeUnregisterDataObserver(observer: Any) {}

    open fun onAfterUnregisterDataObserver(observer: Any) {}
}

abstract class UniversalHolder<out WT>(val itemView: WT, val viewType: Int)

open class UniversalViewHolder(itemView: View, viewType: Int):
        UniversalHolder<View>(itemView, viewType)

open class UniversalRemoteViewHolder(itemView: RemoteViews, viewType: Int):
        UniversalHolder<RemoteViews>(itemView, viewType)

interface UniversalAdapterBase {

    fun hasObservers(): Boolean

    fun hasOnlySimpleDataChangedReporting(): Boolean

    fun notifyDataSetChanged()

    fun notifyItemChanged(position: Int)

    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int)

    fun notifyItemInserted(position: Int)

    fun notifyItemMoved(fromPosition: Int, toPosition: Int)

    fun notifyItemRangeInserted(positionStart: Int, itemCount: Int)

    fun notifyItemRemoved(position: Int)

    fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int)
}

private object EmptyUniversalAdapterBase : UniversalAdapterBase {

    override fun hasObservers(): Boolean = false

    override fun hasOnlySimpleDataChangedReporting(): Boolean = true

    override fun notifyDataSetChanged() {}

    override fun notifyItemChanged(position: Int) {}

    override fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) {}

    override fun notifyItemInserted(position: Int) {}

    override fun notifyItemMoved(fromPosition: Int, toPosition: Int) {}

    override fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {}

    override fun notifyItemRemoved(position: Int) {}

    override fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int) {}
}

abstract class SimpleReportingUniversalAdapterBase : UniversalAdapterBase {

    override fun hasOnlySimpleDataChangedReporting(): Boolean = true

    override fun notifyItemChanged(position: Int) =
            notifyDataSetChanged()

    override fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) =
            notifyDataSetChanged()

    override fun notifyItemInserted(position: Int) =
            notifyDataSetChanged()

    override fun notifyItemMoved(fromPosition: Int, toPosition: Int) =
            notifyDataSetChanged()

    override fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) =
            notifyDataSetChanged()

    override fun notifyItemRemoved(position: Int) =
            notifyDataSetChanged()

    override fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int) =
            notifyDataSetChanged()
}

class UniversalListBase<VH : UniversalViewHolder>(private val adapter: UniversalAdapter<VH>) :
        SimpleReportingUniversalAdapterBase(), ListAdapter, SpinnerAdapter {

    companion object {

        private const val LOG_TAG = "UniversalListBase"
        private const val VIEW_TAG_KEY_VIEW_HOLDER = "$LOG_TAG.VIEW_HOLDER"
    }

    private val observable = DataObservable()

    init { adapter.base = this }

    override fun areAllItemsEnabled(): Boolean = true

    override fun isEnabled(position: Int): Boolean = true

    override fun registerDataSetObserver(observer: DataSetObserver) {
        if (!observable.hasObservers()) adapter.onAttachToContainer(null)

        adapter.onBeforeRegisterDataObserver(observer)
        observable.registerObserver(observer)
        adapter.onAfterRegisterDataObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        adapter.onBeforeUnregisterDataObserver(observer)
        observable.unregisterObserver(observer)
        adapter.onAfterUnregisterDataObserver(observer)

        if (!observable.hasObservers()) adapter.onDetachFromContainer(null)
    }

    override fun hasObservers(): Boolean {
        return observable.hasObservers()
    }

    override fun notifyDataSetChanged() {
        adapter.onDataSetChanged()
        observable.notifyChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val requiredViewType = adapter.getItemViewType(position)

        val viewHolder: VH = convertView?.let {
            @Suppress("UNCHECKED_CAST")
            it.getTag(VIEW_TAG_KEY_VIEW_HOLDER) as? VH
        }.let {
                    if (it == null || it.viewType != requiredViewType) {
                        adapter.onCreateViewHolder(parent, requiredViewType).also {
                            if (it.viewType != requiredViewType)
                                throw IllegalStateException("UniversalViewHolder returned by " +
                                        "UniversalAdapter.onCreateViewHolder() has invalid viewType.")
                            it.itemView.setTag(VIEW_TAG_KEY_VIEW_HOLDER, it)
                        }
                    } else it
                }

        adapter.onBindViewHolder(viewHolder, position)
        return viewHolder.itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
            getView(position, convertView, parent)

    override fun getCount(): Int = adapter.itemCount

    override fun getItem(position: Int): Any = adapter.getItem(position)

    override fun getItemId(position: Int): Long = adapter.getItemId(position)

    override fun hasStableIds(): Boolean = adapter.hasStableIds()

    override fun getItemViewType(position: Int): Int = 0

    override fun getViewTypeCount(): Int = 1

    override fun isEmpty(): Boolean = adapter.isEmpty

    private class DataObservable : DataSetObservable() {

        fun hasObservers(): Boolean = !mObservers.isEmpty()
    }
}

class UniversalRecyclerBase<VH : UniversalViewHolder>(
        private val adapter: UniversalAdapter<VH>
) : UniversalAdapterBase {

    val base: RecyclerAdapter<VH>

    init {
        adapter.base = this
        base = RecyclerAdapter(adapter)
    }

    override fun hasObservers(): Boolean = base.hasObservers()

    override fun hasOnlySimpleDataChangedReporting(): Boolean = false

    override fun notifyDataSetChanged() {
        adapter.onDataSetChanged()
        base.notifyDataSetChanged()
    }

    override fun notifyItemChanged(position: Int) {
        adapter.onDataSetChanged()
        base.notifyItemChanged(position)
    }

    override fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        adapter.onDataSetChanged()
        base.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun notifyItemInserted(position: Int) {
        adapter.onDataSetChanged()
        base.notifyItemInserted(position)
    }

    override fun notifyItemMoved(fromPosition: Int, toPosition: Int) {
        adapter.onDataSetChanged()
        base.notifyItemMoved(fromPosition, toPosition)
    }

    override fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapter.onDataSetChanged()
        base.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun notifyItemRemoved(position: Int) {
        adapter.onDataSetChanged()
        base.notifyItemRemoved(position)
    }

    override fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapter.onDataSetChanged()
        base.notifyItemRangeRemoved(positionStart, itemCount)
    }

    class RecyclerViewHolder<out VH : UniversalViewHolder>(val universalHolder: VH) :
            RecyclerView.ViewHolder(universalHolder.itemView)

    class RecyclerAdapter<VH : UniversalViewHolder>(private val adapter: UniversalAdapter<VH>) :
            RecyclerView.Adapter<RecyclerViewHolder<VH>>() {

        init {
            setHasStableIds(adapter.hasStableIds())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<VH> {
            return adapter.onCreateViewHolder(parent, viewType).takeIf { it.viewType == viewType }
                    ?.let { RecyclerViewHolder(it) }
                    ?: throw IllegalArgumentException("UniversalViewHolder returned by " +
                            "UniversalAdapter.onCreateViewHolder() has invalid viewType.")
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder<VH>, position: Int) {
            adapter.onBindViewHolder(holder.universalHolder, position)
        }

        override fun getItemCount(): Int = adapter.itemCount

        override fun getItemId(position: Int): Long = adapter.getItemId(position)

        override fun getItemViewType(position: Int): Int = adapter.getItemViewType(position)

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            adapter.onAttachToContainer(recyclerView)
            super.onAttachedToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            adapter.onDetachFromContainer(recyclerView)
        }

        override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
            adapter.onBeforeRegisterDataObserver(observer)
            super.registerAdapterDataObserver(observer)
            adapter.onAfterRegisterDataObserver(observer)
        }

        override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
            adapter.onBeforeUnregisterDataObserver(observer)
            super.unregisterAdapterDataObserver(observer)
            adapter.onAfterUnregisterDataObserver(observer)
        }
    }
}

class UniversalWidgetBase<VH : UniversalRemoteViewHolder>(
        private val adapter: UniversalAdapter<VH>,
        private val loadingView: RemoteViews?
) : SimpleReportingUniversalAdapterBase(), RemoteViewsService.RemoteViewsFactory {

    companion object {

        private const val LOG_TAG = "UniversalWidgetBase"
    }

    private var isAttached = false
    private val viewHoldersCache = SparseArray<VH>() // TODO: limit size of cache

    init { adapter.base = this }

    override fun hasObservers(): Boolean = isAttached

    override fun notifyDataSetChanged() {
        // ignored
    }

    override fun onCreate() {
        isAttached = true
        adapter.onAttachToContainer(null)
    }

    override fun onDestroy() {
        adapter.onDetachFromContainer(null)
        isAttached = false
    }

    override fun onDataSetChanged() = adapter.onDataSetChanged()

    override fun getCount(): Int = adapter.itemCount

    override fun getViewAt(i: Int): RemoteViews {
        val itemType = adapter.getItemViewType(i)

        val holder: VH = viewHoldersCache.getOrPut(itemType) {
            adapter.onCreateViewHolder(null, itemType).also {
                if (it.viewType != itemType)
                    throw IllegalStateException("UniversalViewHolder returned by " +
                            "UniversalAdapter.onCreateViewHolder() has invalid viewType.")
            }
        }

        adapter.onBindViewHolder(holder, i)

        return holder.itemView
    }

    override fun getLoadingView(): RemoteViews? = loadingView

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = adapter.getItemId(i)

    override fun hasStableIds(): Boolean = adapter.hasStableIds()
}

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

import android.annotation.TargetApi
import android.appwidget.AppWidgetManager
import android.content.Context
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

import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.R
import eu.codetopic.utils.ui.view.ViewUtils
import eu.codetopic.java.utils.JavaExtensions.to
import eu.codetopic.utils.ui.view.ViewExtensions.getTag
import eu.codetopic.utils.ui.view.ViewExtensions.setTag
import eu.codetopic.utils.AndroidExtensions.getOrPut

abstract class UniversalAdapter<VH : UniversalAdapter.ViewHolder> {// TODO: 26.5.16 add WidgetAdapter support

    companion object {

        private const val LOG_TAG = "UniversalAdapter"

        const val VIEW_TAG_WIDGET_ITEM_CLICK_LISTENER =
                "eu.codetopic.utils.ui.container.adapter.$LOG_TAG.VIEW_TAG_WIDGET_ITEM_CLICK_LISTENER"
        const val NO_VIEW_TYPE = 0
    }

    var base: Base = EmptyBase
        @JvmName("attachBase") set(value) {
            if (field != EmptyBase) throw IllegalStateException("$LOG_TAG's base is still attached")
            field = value
            isBaseAttached = true
            onBaseAttached(value)
        }
        get() {
            return if (field == EmptyBase)
                throw IllegalStateException("$LOG_TAG's base is not attached")
            else field
        }

    var isBaseAttached: Boolean = false
        private set

    abstract val itemCount: Int

    open val isEmpty: Boolean
        get() = itemCount == 0

    fun forRecyclerView(): RecyclerView.Adapter<*> = RecyclerBase(this)

    fun forListView(): ListAdapter = ListBase(this)

    fun forSpinner(): SpinnerAdapter = ListBase(this)

    @TargetApi(11)
    fun forWidget(context: Context, appWidgetIds: IntArray): RemoteViewsService.RemoteViewsFactory =
            WidgetBase(context, this, appWidgetIds)

    protected open fun onBaseAttached(base: Base) {}

    abstract fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH

    abstract fun onBindViewHolder(holder: VH, position: Int)

    abstract fun getItem(position: Int): Any

    open fun getItemId(position: Int): Long = position.toLong()

    open fun hasStableIds(): Boolean = false

    @LayoutRes
    open fun getItemViewType(position: Int): Int = NO_VIEW_TYPE

    open fun onAttachToContainer(container: Any?) {}

    open fun onDetachFromContainer(container: Any?) {}

    open fun onBeforeRegisterDataObserver(observer: Any) {}

    open fun onAfterRegisterDataObserver(observer: Any) {}

    open fun onBeforeUnregisterDataObserver(observer: Any) {}

    open fun onAfterUnregisterDataObserver(observer: Any) {}

    interface Base {

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

    object EmptyBase : Base {

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

    open class ViewHolder(val itemView: View, val viewType: Int)

    abstract class SimpleReportingBase : Base {

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

    class ListBase<VH : ViewHolder>(private val adapter: UniversalAdapter<VH>) :
            SimpleReportingBase(), ListAdapter, SpinnerAdapter {

        companion object {

            private const val LOG_TAG = "${UniversalAdapter.LOG_TAG}\$ListBase"
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
                            throw IllegalStateException("ViewHolder returned by " +
                                    "UniversalAdapter.onCreateViewHolder() has invalid viewType.")
                        it.itemView.setTag(VIEW_TAG_KEY_VIEW_HOLDER, it)
                    }
                } else it
            }

            adapter.onBindViewHolder(viewHolder, position)
            return viewHolder.itemView
        }

        override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View =
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

    class RecyclerBase<VH : ViewHolder>(private val adapter: UniversalAdapter<VH>) :
            RecyclerView.Adapter<RecyclerBase.UniversalViewHolder<VH>>(), Base {

        init {
            adapter.base = this
            setHasStableIds(adapter.hasStableIds())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalViewHolder<VH> {
            return adapter.onCreateViewHolder(parent, viewType).takeIf { it.viewType == viewType }
                    ?.let { UniversalViewHolder(it) }
                    ?: throw IllegalArgumentException("ViewHolder returned by " +
                        "UniversalAdapter.onCreateViewHolder() has invalid viewType.")
        }

        override fun onBindViewHolder(holder: UniversalViewHolder<VH>, position: Int) {
            adapter.onBindViewHolder(holder.universalHolder, position)
        }

        override fun getItemCount(): Int = adapter.itemCount

        override fun getItemId(position: Int): Long = adapter.getItemId(position)

        override fun getItemViewType(position: Int): Int = adapter.getItemViewType(position)

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            adapter.onAttachToContainer(recyclerView)
            super.onAttachedToRecyclerView(recyclerView)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
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

        override fun hasOnlySimpleDataChangedReporting(): Boolean = false

        class UniversalViewHolder<out VH : ViewHolder>(val universalHolder: VH) :
                RecyclerView.ViewHolder(universalHolder.itemView)
    }

    @TargetApi(11)
    class WidgetBase<VH : ViewHolder>(private val context: Context,
                                      private val adapter: UniversalAdapter<VH>,
                                      private val appWidgetIds: IntArray?) :
            SimpleReportingBase(), RemoteViewsService.RemoteViewsFactory {

        companion object {

            private const val LOG_TAG = "${UniversalAdapter.LOG_TAG}\$WidgetBase"
        }

        private val viewHoldersCache = SparseArray<VH>() // TODO: limit size of cache

        private var loadingView: RemoteViews? = null

        init { adapter.base = this }

        override fun hasObservers(): Boolean = false

        override fun notifyDataSetChanged() {
            if (appWidgetIds == null) {
                Log.w(LOG_TAG, "notifyDataSetChanged() -> Can't notify" +
                        " about data set change without valid AppWidgetIds")
                return
            }

            AppWidgetManager.getInstance(context)
                    .notifyAppWidgetViewDataChanged(appWidgetIds, R.id.content_list_view)
        }

        override fun onCreate() = adapter.onAttachToContainer(null)

        override fun onDestroy() = adapter.onDetachFromContainer(null)

        override fun onDataSetChanged() {}

        override fun getCount(): Int = adapter.itemCount

        override fun getViewAt(i: Int): RemoteViews {
            val itemType = adapter.getItemViewType(i)

            val holder: VH = viewHoldersCache.getOrPut(itemType) {
                adapter.onCreateViewHolder(null, itemType)
            }

            adapter.onBindViewHolder(holder, i)

            val remoteViews = RemoteViews(context.packageName, R.layout.widget_list_custom_item)
            remoteViews.setImageViewBitmap(
                    R.id.image_view_content,
                    ViewUtils.drawViewToBitmap(holder.itemView, false)
            )

            holder.itemView.getTag(VIEW_TAG_WIDGET_ITEM_CLICK_LISTENER)
                    .to<WidgetItemClickListener>()
                    ?.let {
                        remoteViews.setOnClickPendingIntent(
                                R.id.content_frame_layout,
                                it.getOnClickIntent(context)
                        )
                    }

            return remoteViews
        }

        override fun getLoadingView(): RemoteViews? = loadingView

        fun setLoadingView(loadingView: RemoteViews) {
            this.loadingView = loadingView
        }

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(i: Int): Long = adapter.getItemId(i)

        override fun hasStableIds(): Boolean = adapter.hasStableIds()
    }
}

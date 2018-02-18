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

package eu.codetopic.utils.ui.container.items.custom

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.io.Serializable

import eu.codetopic.java.utils.ArrayTools
import eu.codetopic.utils.ui.container.adapter.UniversalAdapter
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer

abstract class CustomItem : Serializable {

    companion object {

        private const val LOG_TAG = "CustomItem"

        const val NO_POSITION = -1

        fun createViewHolder(context: Context, parent: ViewGroup?,
                             @LayoutRes itemLayoutId: Int): ViewHolder {
            return createViewHolder(context, parent, null, itemLayoutId)
        }

        internal fun createViewHolder(context: Context, parent: ViewGroup?,
                                     parentHolder: ViewHolder?,
                                     @LayoutRes itemLayoutId: Int): ViewHolder {
            return ViewHolder(context, LayoutInflater.from(context)
                    .inflate(itemLayoutId, parent, false), parentHolder, itemLayoutId)
        }
    }

    fun createViewHolder(context: Context, parent: ViewGroup?): ViewHolder {
        return createViewHolder(context, parent, getLayoutResId(context))
    }

    fun bindViewHolder(holder: UniversalAdapter.ViewHolder, itemPosition: Int) {
        bindViewHolder(ViewHolder.fromUniversalHolder(holder), itemPosition)
    }

    fun bindViewHolder(holder: ViewHolder, itemPosition: Int) {
        performBindViewHolder(holder, itemPosition, null, emptyArray())
    }

    internal fun performBindViewHolder(holder: ViewHolder, itemPosition: Int,
                                       contentItem: CustomItem?,
                                       wrappers: Array<CustomItemWrapper>): ViewHolder? {
        // Create array of my wrappers
        val myWrappers = getWrappers(holder.context) + wrappers

        // If there are wrappers, give ViewHolder and array of other wrappers
        //  to first of them and obtain wrapped ViewHolder.
        // If list of wrappers is empty, don't replace ViewHolder
        val myHolder = (myWrappers.firstOrNull()?.let {
            it.performBindViewHolder(
                    holder, itemPosition, this,
                    ArrayTools.remove(myWrappers, 0)
            ).apply {
                if (this == null) throw IllegalStateException(
                        "Invalid holder (item wrapper returned null): $this")
            }
        } ?: holder).apply {
            if (layoutResId != getItemLayoutResId(context))
                throw IllegalStateException("Invalid holder (wrong layout): " +
                        "(required=${getItemLayoutResId(context)}, received=$layoutResId," +
                        " holder=$this)")
        }

        val contentHolder: ViewHolder? = (this as? CustomItemWrapper)
                ?.getContentHolder(myHolder, contentItem)

        onBindViewHolder(myHolder, itemPosition)
        return contentHolder
    }

    protected abstract fun onBindViewHolder(holder: ViewHolder, itemPosition: Int)

    @LayoutRes
    fun getLayoutResId(context: Context): Int {
        return getWrappers(context).lastOrNull()?.getLayoutResId(context)
                ?: getItemLayoutResId(context)
    }

    @LayoutRes
    abstract fun getItemLayoutResId(context: Context): Int

    fun usesWrapper(context: Context, wrapperClass: Class<out CustomItemWrapper>): Boolean {
        return getWrappers(context).any { it.javaClass == wrapperClass }
    }

    protected open fun getWrappers(context: Context): Array<CustomItemWrapper> = emptyArray()

    @ContainerOptions(CacheImplementation.SPARSE_ARRAY)
    class ViewHolder internal constructor(val context: Context, val itemView: View,
                                          val parentHolder: ViewHolder?,
                                          @param:LayoutRes @field:LayoutRes val layoutResId: Int) : LayoutContainer {

        companion object {

            fun fromUniversalHolder(holder: UniversalAdapter.ViewHolder): ViewHolder {
                if (holder !is ViewHolder.UniversalHolder)
                    throw IllegalArgumentException("Invalid holder (unknown holder): ${holder.javaClass}")
                return holder.base
            }
        }

        override val containerView: View get() = itemView

        val forUniversalAdapter: UniversalAdapter.ViewHolder
                by lazy { UniversalHolder(this) }
            @JvmName("forUniversalAdapter") get

        val topParentHolder: ViewHolder
            get() { return (parentHolder ?: return this).topParentHolder }

        internal class UniversalHolder(val base: ViewHolder) :
                UniversalAdapter.ViewHolder(base.itemView, base.layoutResId)
    }

}

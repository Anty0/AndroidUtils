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

package eu.codetopic.utils.ui.container.items.custom

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import eu.codetopic.utils.R
import eu.codetopic.utils.ui.container.adapter.UniversalRemoteViewHolder
import eu.codetopic.utils.ui.container.adapter.UniversalViewHolder
import eu.codetopic.utils.ui.view.ViewUtils
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.extensions.LayoutContainer
import java.io.Serializable

/**
 * @author anty
 */

abstract class CustomItem : Serializable {

    companion object {

        private const val LOG_TAG = "CustomItem"

        const val NO_POSITION = -1

        internal fun createViewHolder(context: Context, parent: ViewGroup?,
                                      @LayoutRes layoutId: Int): CustomItemViewHolder {
            val itemView = LayoutInflater.from(context)
                    .inflate(layoutId, parent, false)
            return CustomItemViewHolder(context, itemView, layoutId)
        }

        internal fun createRemoteViewHolder(
                context: Context,
                @LayoutRes layoutId: Int
        ): CustomItemRemoteViewHolder {
            val itemView = RemoteViews(context.packageName, layoutId)
            return CustomItemRemoteViewHolder(context, itemView, layoutId)
        }
    }

    fun createViewHolder(context: Context, parent: ViewGroup?): CustomItemViewHolder {
        return createViewHolder(context, parent, getLayoutResId(context))
    }

    fun createRemoteViewHolder(context: Context): CustomItemRemoteViewHolder {
        return createRemoteViewHolder(context, getRemoteLayoutResId(context))
    }

    fun bindViewHolder(holder: UniversalViewHolder, itemPosition: Int) {
        bindViewHolder(CustomItemViewHolder.fromUniversalHolder(holder), itemPosition)
    }

    fun bindRemoteViewHolder(holder: UniversalRemoteViewHolder, itemPosition: Int) {
        bindRemoteViewHolder(CustomItemRemoteViewHolder.fromUniversalHolder(holder), itemPosition)
    }

    fun bindViewHolder(holder: CustomItemViewHolder, itemPosition: Int) {
        if (holder.layoutResId != getLayoutResId(holder.context))
            throw IllegalArgumentException("Invalid holder (invalid holder's layoutResId)")

        onBindViewHolder(holder, itemPosition)
    }

    fun bindRemoteViewHolder(holder: CustomItemRemoteViewHolder, itemPosition: Int) {
        if (holder.layoutResId != getRemoteLayoutResId(holder.context))
            throw IllegalArgumentException("Invalid holder (invalid holder's layoutResId)")

        onBindRemoteViewHolder(holder, itemPosition)
    }

    protected abstract fun onBindViewHolder(holder: CustomItemViewHolder, itemPosition: Int)

    protected open fun onBindRemoteViewHolder(
            holder: CustomItemRemoteViewHolder,
            itemPosition: Int
    ) {
        if (holder.layoutResId != R.layout.widget_list_custom_item)
            throw RuntimeException("Remote layout id was modified, can't bind remote view.")

        val viewHolder = createViewHolder(holder.context, null)
                .also { onBindViewHolder(it, itemPosition) }

        holder.itemView.setImageViewBitmap(
                R.id.image_view_content,
                ViewUtils.drawViewToBitmap(viewHolder.itemView, false)
        )
    }

    @LayoutRes
    abstract fun getLayoutResId(context: Context): Int

    @LayoutRes
    open fun getRemoteLayoutResId(context: Context): Int = R.layout.widget_list_custom_item
}

@ContainerOptions(CacheImplementation.SPARSE_ARRAY)
class CustomItemViewHolder internal constructor(
        val context: Context,
        val itemView: View,
        @param:LayoutRes @field:LayoutRes val layoutResId: Int
) : LayoutContainer {

    companion object {

        fun fromUniversalHolder(holder: UniversalViewHolder): CustomItemViewHolder {
            if (holder !is CustomUniversalViewHolder)
                throw IllegalArgumentException("Invalid holder (unknown holder): ${holder.javaClass}")
            return holder.base
        }
    }

    override val containerView: View get() = itemView

    val forUniversalAdapter: UniversalViewHolder
            by lazy { CustomUniversalViewHolder(this) }
        @JvmName("forUniversalAdapter") get

    internal class CustomUniversalViewHolder(val base: CustomItemViewHolder) :
            UniversalViewHolder(base.itemView, base.layoutResId)
}

class CustomItemRemoteViewHolder internal constructor(
        val context: Context,
        val itemView: RemoteViews,
        @param:LayoutRes @field:LayoutRes val layoutResId: Int
) {

    companion object {

        fun fromUniversalHolder(holder: UniversalRemoteViewHolder): CustomItemRemoteViewHolder {
            if (holder !is CustomUniversalHolder)
                throw IllegalArgumentException("Invalid holder (unknown holder): ${holder.javaClass}")
            return holder.base
        }
    }

    val forUniversalAdapter: UniversalRemoteViewHolder
            by lazy { CustomUniversalHolder(this) }
        @JvmName("forUniversalAdapter") get

    internal class CustomUniversalHolder(val base: CustomItemRemoteViewHolder) :
            UniversalRemoteViewHolder(base.itemView, base.layoutResId)
}
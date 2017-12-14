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
import android.support.annotation.IdRes
import android.view.ViewGroup
import eu.codetopic.utils.ui.view.ViewUtils
import eu.codetopic.utils.ui.view.ViewExtensions.getTag
import eu.codetopic.utils.ui.view.ViewExtensions.setTag

abstract class CustomItemWrapper : CustomItem() {

    companion object {

        private const val LOG_TAG = "CustomItemWrapper"
        private const val VIEW_TAG_KEY_CONTENT_VIEW_HOLDER = "$LOG_TAG.WRAPPER_VIEW_HOLDER"
    }

    @IdRes
    protected abstract fun getContentViewId(context: Context): Int

    internal fun getContentHolder(holder: ViewHolder, contentItem: CustomItem?): ViewHolder? {
        if (holder.layoutResId != getItemLayoutResId(holder.context))
            throw IllegalStateException("Invalid holder (wrong layout): " +
                    "(required=${getItemLayoutResId(holder.context)}, received=${holder.layoutResId}," +
                    " holder=$this)")

        @IdRes val contentId = getContentViewId(holder.context)
        val content = holder.itemView.findViewById<ViewGroup>(contentId)
        var contentHolder = content.getTag(VIEW_TAG_KEY_CONTENT_VIEW_HOLDER) as ViewHolder?

        if (contentItem == null || contentHolder != null && contentHolder.layoutResId !=
                contentItem.getItemLayoutResId(contentHolder.context)) {
            content.setTag(VIEW_TAG_KEY_CONTENT_VIEW_HOLDER, null)
            content.removeAllViews()
            contentHolder = null
        }
        if (contentItem != null && contentHolder == null) {
            contentHolder = createViewHolder(holder.context, content, holder,
                    contentItem.getItemLayoutResId(holder.context))
            content.addView(contentHolder.itemView)
            content.setTag(VIEW_TAG_KEY_CONTENT_VIEW_HOLDER, contentHolder)
        }

        return contentHolder
    }

}

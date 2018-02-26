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

/**
 * @author anty
 */

open class LayoutItem(
        @param:LayoutRes @field:LayoutRes @get:LayoutRes private val layoutRes: Int
) : CustomItem() {

    companion object {

        private const val LOG_TAG = "LayoutItem"
    }

    override fun onBindViewHolder(holder: CustomItemViewHolder, itemPosition: Int) {}

    override fun getLayoutResId(context: Context): Int = layoutRes
}

open class WidgetLayoutItem(@LayoutRes layoutRes: Int) : LayoutItem(layoutRes) {

    companion object {

        private const val LOG_TAG = "WidgetLayoutItem"
    }

    override fun onBindRemoteViewHolder(holder: CustomItemRemoteViewHolder, itemPosition: Int) {}

    override fun getRemoteLayoutResId(context: Context): Int = getLayoutResId(context)
}

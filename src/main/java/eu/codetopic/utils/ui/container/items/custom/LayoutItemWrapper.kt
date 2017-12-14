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
import android.support.annotation.LayoutRes

open class LayoutItemWrapper(@param:LayoutRes @field:LayoutRes private val layoutRes: Int,
                             @param:IdRes @field:IdRes private val contentViewId: Int) : CustomItemWrapper() {

    companion object {

        private const val LOG_TAG = "LayoutItemWrapper"
    }

    override fun onBindViewHolder(holder: CustomItem.ViewHolder, itemPosition: Int) {}

    override fun getItemLayoutResId(context: Context): Int = layoutRes

    override fun getContentViewId(context: Context): Int = contentViewId
}

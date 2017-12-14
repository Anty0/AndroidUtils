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

import eu.codetopic.utils.R
import eu.codetopic.utils.ui.view.ViewUtils
import kotlinx.android.synthetic.main.item_wrapper_card_view.*

class CardViewWrapper : LayoutItemWrapper(R.layout.item_wrapper_card_view, R.id.card_view) {

    companion object {

        private const val LOG_TAG = "CardViewWrapper"

        val WRAPPER = arrayOf<CustomItemWrapper>(CardViewWrapper())
    }

    override fun onBindViewHolder(holder: CustomItem.ViewHolder, itemPosition: Int) {
        holder.card_view.takeIf { it.childCount == 1 }?.run {
            ViewUtils.copyLayoutParamsToViewParents(getChildAt(0), holder.itemView)
        }
    }
}

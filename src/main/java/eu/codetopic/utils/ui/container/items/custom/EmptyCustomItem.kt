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
import android.view.View

import eu.codetopic.utils.R

class EmptyCustomItem : CustomItem() {

    override fun onBindViewHolder(holder: CustomItem.ViewHolder, itemPosition: Int) {
        // Completely hide item, so there will nothing stay in layout
        holder.itemView.visibility = View.GONE
    }

    override fun getItemLayoutResId(context: Context): Int = R.layout.item_empty
}

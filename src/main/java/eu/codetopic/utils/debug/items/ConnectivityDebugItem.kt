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

package eu.codetopic.utils.debug.items

import android.content.Context
import android.view.View
import android.widget.TextView

import eu.codetopic.utils.AndroidExtensions.getFormattedText
import eu.codetopic.utils.network.NetworkManager
import eu.codetopic.utils.R
import eu.codetopic.utils.network.NetworkManager.NetworkType.*
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper

class ConnectivityDebugItem : CustomItem() {

    companion object {

        private const val LOG_TAG = "ConnectivityDebugItem"
    }

    override fun onBindViewHolder(holder: CustomItem.ViewHolder, itemPosition: Int) {
        val resultText = holder.itemView.findViewById<TextView>(R.id.resultText)

        holder.itemView.findViewById<View>(R.id.checkButton).setOnClickListener {
            resultText.visibility = View.VISIBLE
            resultText.text = resultText.context
                    .getFormattedText(R.string.debug_item_connectivity_info_result_text,
                            NetworkManager.isConnected(ANY).toString(),
                            NetworkManager.isConnected(WIFI).toString(),
                            NetworkManager.isConnected(MOBILE).toString())
        }
    }

    override fun getItemLayoutResId(context: Context) = R.layout.item_debug_connectivity

    override fun getWrappers(context: Context): Array<CustomItemWrapper> = CardViewWrapper.WRAPPER
}

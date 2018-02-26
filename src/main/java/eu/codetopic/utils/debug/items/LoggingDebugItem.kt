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

import eu.codetopic.java.utils.log.Logger
import eu.codetopic.utils.R
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.items.custom.CustomItemViewHolder

class LoggingDebugItem : CustomItem() {

    companion object {

        private const val LOG_TAG = "LoggingDebugItem"
    }

    override fun onBindViewHolder(holder: CustomItemViewHolder, itemPosition: Int) {
        val resultText = holder.itemView.findViewById<TextView>(R.id.resultText)

        holder.itemView.findViewById<View>(R.id.refreshButton).setOnClickListener {
            resultText.visibility = View.VISIBLE
            resultText.text = Logger.cachedLogLines
        }
    }

    override fun getLayoutResId(context: Context) = R.layout.item_debug_logging
}

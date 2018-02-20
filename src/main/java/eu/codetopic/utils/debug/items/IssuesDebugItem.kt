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

package eu.codetopic.utils.debug.items

import android.content.Context
import eu.codetopic.java.utils.log.Log
import eu.codetopic.utils.R
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper
import eu.codetopic.utils.ui.container.items.custom.CustomItem
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper
import kotlinx.android.synthetic.main.item_debug_issues.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @author anty
 */
class IssuesDebugItem : CustomItem() {

    companion object {

        private const val LOG_TAG = "IssuesDebugItem"
    }

    override fun onBindViewHolder(holder: CustomItem.ViewHolder, itemPosition: Int) {
        holder.butLogError.onClick {
            Log.e(LOG_TAG, "Test message", RuntimeException("Test exception"))
        }

        holder.butLogWarning.onClick {
            Log.w(LOG_TAG, "Test warning message")
        }

        holder.butLogBreakEvent.onClick {
            Log.b(LOG_TAG, "Test event message")
        }
    }

    override fun getItemLayoutResId(context: Context) = R.layout.item_debug_issues

    override fun getWrappers(context: Context): Array<CustomItemWrapper> = CardViewWrapper.WRAPPER
}
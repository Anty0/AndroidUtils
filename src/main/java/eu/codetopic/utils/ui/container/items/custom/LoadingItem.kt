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
import android.support.annotation.StringRes
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import eu.codetopic.utils.R

/**
 * @author anty
 */
open class LoadingItem @JvmOverloads constructor(private val title: CharSequence,
                                                 private val text: CharSequence? = null) :
        CustomItem() {

    @JvmOverloads
    constructor(context: Context, @StringRes titleId: Int, @StringRes textId: Int? = null)
            : this(context.getText(titleId), textId?.let { context.getText(it) })

    override fun onBindViewHolder(holder: ViewHolder, itemPosition: Int) {
        val txtTitle: TextView = holder.itemView.findViewById(R.id.txtTitle)
        val txtText: TextView = holder.itemView.findViewById(R.id.txtText)

        txtTitle.text = title

        txtText.text = text
        txtText.visibility = if (text != null) VISIBLE else GONE
    }

    override fun getItemLayoutResId(context: Context?): Int = R.layout.item_loading
}
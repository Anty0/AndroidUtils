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
import android.support.annotation.StringRes
import android.view.View
import eu.codetopic.utils.R
import kotlinx.android.synthetic.main.item_loading.view.*

/**
 * @author anty
 */

open class LoadingItem(private val title: CharSequence,
                       private val subtitle: CharSequence? = null) : CustomItem() {

    constructor(context: Context, @StringRes titleId: Int, @StringRes subtitleId: Int? = null)
            : this(context.getText(titleId), subtitleId?.let { context.getText(it) })

    override fun onBindViewHolder(holder: CustomItemViewHolder, itemPosition: Int) {
        holder.itemView.txtTitle.text = title

        holder.itemView.txtText.apply {
            text = this@LoadingItem.subtitle
            visibility = if (text != null) View.VISIBLE else View.GONE
        }
    }

    override fun onBindRemoteViewHolder(holder: CustomItemRemoteViewHolder, itemPosition: Int) {
        holder.itemView.setTextViewText(R.id.txtTitle, title)

        holder.itemView.setTextViewText(R.id.txtText, subtitle)
        holder.itemView.setViewVisibility(
                R.id.txtText,
                if (subtitle != null) View.VISIBLE else View.GONE
        )
    }

    override fun getLayoutResId(context: Context): Int = R.layout.item_loading

    override fun getRemoteLayoutResId(context: Context): Int = R.layout.item_loading_small
}

open class CardLoadingItem(private val title: CharSequence,
                           private val subtitle: CharSequence? = null) : CustomItem() {

    constructor(context: Context, @StringRes titleId: Int, @StringRes subtitleId: Int? = null)
            : this(context.getText(titleId), subtitleId?.let { context.getText(it) })

    override fun onBindViewHolder(holder: CustomItemViewHolder, itemPosition: Int) {
        holder.itemView.txtTitle.text = title

        holder.itemView.txtText.apply {
            text = this@CardLoadingItem.subtitle
            visibility = if (text != null) View.VISIBLE else View.GONE
        }
    }

    override fun getLayoutResId(context: Context): Int = R.layout.item_loading_card
}

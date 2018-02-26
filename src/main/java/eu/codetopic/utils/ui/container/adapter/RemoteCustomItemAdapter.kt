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

package eu.codetopic.utils.ui.container.adapter

import android.content.Context
import android.view.ViewGroup

import eu.codetopic.utils.ui.container.items.custom.CustomItem

open class RemoteCustomItemAdapter<T : CustomItem> : ArrayEditAdapter<T, UniversalRemoteViewHolder> {

    companion object {

        private const val LOG_TAG = "RemoteCustomItemAdapter"
    }

    val context: Context

    constructor(context: Context) : super() { this.context = context }

    constructor(context: Context, data: Collection<T>) : super(data) { this.context = context }

    constructor(context: Context, vararg data: T) : super(*data) { this.context = context }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UniversalRemoteViewHolder =
            CustomItem.createRemoteViewHolder(context, viewType).forUniversalAdapter

    override fun onBindViewHolder(holder: UniversalRemoteViewHolder, position: Int) =
            getItem(position).bindRemoteViewHolder(holder, position)

    override fun getItemViewType(position: Int): Int =
            getItem(position).getRemoteLayoutResId(context)
}

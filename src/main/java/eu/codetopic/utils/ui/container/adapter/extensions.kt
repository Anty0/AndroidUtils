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

package eu.codetopic.utils.ui.container.adapter

import android.support.v7.widget.RecyclerView
import android.widget.ListAdapter
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import android.widget.SpinnerAdapter

/**
 * @author anty
 */

fun <VH : UniversalViewHolder> UniversalAdapter<VH>.forRecyclerView(): RecyclerView.Adapter<*> =
        UniversalRecyclerBase(this).base

fun <VH : UniversalViewHolder> UniversalAdapter<VH>.forListView(): ListAdapter =
        UniversalListBase(this)

fun <VH : UniversalViewHolder> UniversalAdapter<VH>.forSpinner(): SpinnerAdapter =
        UniversalListBase(this)

fun <VH : UniversalRemoteViewHolder> UniversalAdapter<VH>.forWidget(
        loadingView: RemoteViews? = null
): RemoteViewsFactory = UniversalWidgetBase(this, loadingView)

typealias Modification<E> = MutableList<E>.(base: UniversalAdapterBase?) -> Unit
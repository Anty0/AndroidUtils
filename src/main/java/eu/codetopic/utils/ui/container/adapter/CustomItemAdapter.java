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

package eu.codetopic.utils.ui.container.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.Collection;

import eu.codetopic.utils.ui.container.items.custom.CustomItem;

public class CustomItemAdapter<T extends CustomItem> extends
        ArrayEditAdapter<T, UniversalAdapter.ViewHolder> {

    private static final String LOG_TAG = "CustomItemAdapter";

    private final Context mContext;

    public CustomItemAdapter(Context context) {
        super();
        mContext = context;
    }

    public CustomItemAdapter(Context context, Collection<? extends T> data) {
        super(data);
        mContext = context;
    }

    @SafeVarargs
    public CustomItemAdapter(Context context, T... data) {
        super(data);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CustomItem.createViewHolder(getContext(), parent, viewType).forUniversalAdapter();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getItem(position).bindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getLayoutResId(getContext());
    }
}

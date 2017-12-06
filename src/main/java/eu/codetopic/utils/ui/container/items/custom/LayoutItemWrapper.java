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

package eu.codetopic.utils.ui.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

public class LayoutItemWrapper extends CustomItemWrapper { // TODO: after rework to kotlin remove

    private static final String LOG_TAG = "LayoutItemWrapper";

    @LayoutRes private final int layoutRes;
    @IdRes private final int contentViewId;
    private final CustomItemWrapper[] wrappers;

    public LayoutItemWrapper(@LayoutRes int layoutRes, @IdRes int contentViewId,
                             @NonNull CustomItemWrapper... wrappers) {
        this.layoutRes = layoutRes;
        this.contentViewId = contentViewId;
        this.wrappers = wrappers;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
    }

    @Override
    public int getItemLayoutResId(Context context) {
        return layoutRes;
    }

    @Override
    protected int getContentViewId(Context context) {
        return contentViewId;
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return wrappers;
    }
}

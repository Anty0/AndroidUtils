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

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.view.ViewUtils;

public class CardViewWrapper extends LayoutItemWrapper { // TODO: deprecate after rework to kotlin

    public static final CustomItemWrapper[] WRAPPER = {new CardViewWrapper()};
    private static final String LOG_TAG = "CardViewWrapper";

    public CardViewWrapper(@NonNull CustomItemWrapper... wrappers) {
        super(R.layout.card_view_base, R.id.card_view, wrappers);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        ViewGroup content = holder.itemView.findViewById(getContentViewId(holder.context));
        if (content.getChildCount() == 1)
            ViewUtils.copyLayoutParamsToViewParents(content.getChildAt(0), holder.itemView);
    }
}

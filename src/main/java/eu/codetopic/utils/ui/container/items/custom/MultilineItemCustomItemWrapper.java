/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.codetopic.utils.ui.container.items.multiline.MultilineItem;
import eu.codetopic.utils.ui.container.items.multiline.MultilineItemUtils;

public class MultilineItemCustomItemWrapper extends CustomItem {

    private static final String LOG_TAG = "MultilineItemCustomItemWrapper";

    private final MultilineItem item;
    private final CustomItemWrapper[] wrappers;

    public MultilineItemCustomItemWrapper(@Nullable MultilineItem item, @NonNull CustomItemWrapper... wrappers) {
        if (item == null && this instanceof MultilineItem) this.item = (MultilineItem) this;
        else this.item = item;
        this.wrappers = wrappers;
    }

    public static List<MultilineItemCustomItemWrapper> wrapAll(Collection<? extends MultilineItem> items,
                                                               @NonNull CustomItemWrapper... additionalWrappers) {

        List<MultilineItemCustomItemWrapper> wrappedItems = new ArrayList<>();
        for (MultilineItem item : items)
            wrappedItems.add(new MultilineItemCustomItemWrapper(item, additionalWrappers));
        return wrappedItems;
    }

    public MultilineItem getItemBase() {
        return item;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int itemPosition) {
        MultilineItemUtils.apply(item).withoutPadding()
                .withDefaultLayoutResId(getLayoutResId(holder.context))
                .withPosition(itemPosition).on(holder.itemView);
    }

    @Override
    @LayoutRes
    public int getItemLayoutResId(Context context) {
        return MultilineItemUtils.getLayoutResIdFor(context, item, null);
    }

    @NonNull
    @Override
    protected CustomItemWrapper[] getWrappers(Context context) {
        return wrappers;
    }
}

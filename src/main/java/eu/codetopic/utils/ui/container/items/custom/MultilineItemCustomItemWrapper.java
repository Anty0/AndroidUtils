/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

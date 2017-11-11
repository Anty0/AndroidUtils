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

package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;

import eu.codetopic.utils.ui.container.items.custom.CustomItem;

public class SimpleItemInfo extends ItemInfo {

    private static final String LOG_TAG = "SimpleItemInfo";
    private final CustomItem item;
    private final int priority;

    public SimpleItemInfo(CustomItem item, int priority) {
        this.item = item;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @NonNull
    @Override
    public CustomItem getItem(Context context) {
        return item;
    }
}

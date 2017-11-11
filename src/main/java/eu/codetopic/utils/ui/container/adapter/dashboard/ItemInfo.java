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
import android.support.annotation.UiThread;

import eu.codetopic.utils.ui.container.items.custom.CustomItem;

public abstract class ItemInfo implements Comparable<ItemInfo> {

    private boolean enabled = getDefaultEnabledState();

    public ItemInfo() {
        DashboardData.getter.get().restoreItemState(this);
    }

    @Override
    public int compareTo(@NonNull ItemInfo another) {
        return another.getPriority() - getPriority();
    }

    public abstract int getPriority();

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
        DashboardData.getter.get().saveItemState(this);
    }

    public boolean getDefaultEnabledState() {
        return true;
    }

    protected void onRestoreEnabledState(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean hasPersistentEnabledState() {
        return false;
    }

    public String getSaveName() {
        return null;
    }

    @NonNull
    @UiThread
    public abstract CustomItem getItem(Context context);
}

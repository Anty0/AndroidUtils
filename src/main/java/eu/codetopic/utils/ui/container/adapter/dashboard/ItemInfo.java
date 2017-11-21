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

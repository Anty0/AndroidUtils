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

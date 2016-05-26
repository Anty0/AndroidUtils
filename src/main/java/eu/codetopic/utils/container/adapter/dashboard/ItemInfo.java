package eu.codetopic.utils.container.adapter.dashboard;

import android.support.annotation.NonNull;

import eu.codetopic.utils.container.items.custom.CustomItem;

public abstract class ItemInfo implements Comparable<ItemInfo> {

    private boolean enabled = getDefaultEnabledState();

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
    }

    public boolean getDefaultEnabledState() {
        return true;
    }

    protected void onRestoreEnabledState(boolean enabled) {
        setEnabled(enabled);
    }

    public boolean hasPersistentEnabledState() {
        return false;
    }

    public String getSaveName() {
        return null;
    }

    public abstract CustomItem getItem();
}

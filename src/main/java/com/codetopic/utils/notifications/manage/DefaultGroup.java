package com.codetopic.utils.notifications.manage;

public class DefaultGroup extends Group {

    private static final String LOG_TAG = "DefaultGroup";

    public DefaultGroup(String name) {
        super(name);
    }

    @Override
    protected int getNewId() {
        int id = getFirstFreeId();
        addIdToCache(id);
        return id;
    }

    @Override
    protected void onIdRemoved(int id) {
        removeIdFromCache(id);
    }

}

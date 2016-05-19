package eu.codetopic.utils.notifications.manage;

public abstract class SingleIdGroup extends Group {

    public static final int NO_ID = -1;
    private static final String LOG_TAG = "SingleIdGroup";

    public SingleIdGroup(String name) {
        super(name);
    }

    /**
     * Called before notification will be showed.
     * Generates only single id and uses it every next call.
     *
     * @return id for new notification
     */
    @Override
    protected int getNewId() {
        int[] ids = getIdsFromCache();
        if (ids.length == 0) {
            addIdToCache(getFirstFreeId());
            return getNewId();
        }
        return ids[0];
    }

    /**
     * Called when notification was canceled.
     *
     * @param id id of removed notification or NO_ID
     */
    @Override
    protected void onIdRemoved(int id) {
        //do nothing
    }
}

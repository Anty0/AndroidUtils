package eu.codetopic.utils.log.base;

public enum Priority {

    VERBOSE(android.util.Log.VERBOSE), DEBUG(android.util.Log.DEBUG),
    INFO(android.util.Log.INFO), WARN(android.util.Log.WARN),
    ERROR(android.util.Log.ERROR), ASSERT(android.util.Log.ASSERT);

    private final int systemId;

    Priority(int systemId) {
        this.systemId = systemId;
    }

    public int getSystemId() {
        return systemId;
    }

}

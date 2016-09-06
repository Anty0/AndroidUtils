package eu.codetopic.utils.log.base;

public enum Priority {

    VERBOSE(android.util.Log.VERBOSE, 'V'), DEBUG(android.util.Log.DEBUG, 'D'),
    INFO(android.util.Log.INFO, 'I'), WARN(android.util.Log.WARN, 'W'),
    ERROR(android.util.Log.ERROR, 'E'), ASSERT(android.util.Log.ASSERT, 'A');

    private final int systemId;
    private final char disaplayID;

    Priority(int systemId, char disaplayID) {
        this.systemId = systemId;
        this.disaplayID = disaplayID;
    }

    public int getSystemId() {
        return systemId;
    }

    public char getDisaplayID() {
        return disaplayID;
    }
}

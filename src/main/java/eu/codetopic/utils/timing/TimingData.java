package eu.codetopic.utils.timing;

import android.content.Context;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.preferences.SharedPreferencesData;

public final class TimingData extends SharedPreferencesData {

    private static final int SAVE_VERSION = 0;

    public TimingData(Context context) {
        super(context, PrefNames.FILE_NAME_TIMING_DATA, SAVE_VERSION);
    }

    public void clear() {
        edit().clear().apply();
    }

    public void clear(Class clazz) {
        edit().remove(clazz.getName() + PrefNames.ADD_TIME_LAST_START)
                .remove(clazz.getName() + PrefNames.ADD_LAST_BROADCAST_REQUEST_CODE).apply();
    }

    public long getLastExecuteTime(Class clazz) {
        return getPreferences().getLong(clazz.getName() + PrefNames.ADD_TIME_LAST_START, -1L);
    }

    void setLastExecuteTime(Class clazz, long lastExecuteTime) {
        edit().putLong(clazz.getName() + PrefNames.ADD_TIME_LAST_START, lastExecuteTime).apply();
    }

    public int getLastRequestCode(Class clazz) {
        return getPreferences().getInt(clazz.getName() + PrefNames.ADD_LAST_BROADCAST_REQUEST_CODE, -1);
    }

    void setLastRequestCode(Class clazz, int lastRequestCode) {
        edit().putInt(clazz.getName() + PrefNames.ADD_LAST_BROADCAST_REQUEST_CODE, lastRequestCode).apply();
    }
}

package eu.codetopic.utils.timing;

import android.content.Context;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.module.data.ModuleData;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public final class TimingData extends ModuleData {

    private static final int SAVE_VERSION = 0;

    public TimingData(Context context) {
        super(context, PrefNames.FILE_NAME_TIMING_DATA, false, SAVE_VERSION);
    }

    public void clear() {
        edit().clear().apply();
    }

    public long getLastExecuteTime(Class clazz) {
        return getPreferences().getLong(clazz.getName() + PrefNames.ADD_TIME_LAST_START, -1);
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

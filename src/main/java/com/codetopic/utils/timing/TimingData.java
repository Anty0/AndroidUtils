package com.codetopic.utils.timing;

import android.content.Context;

import com.codetopic.utils.PrefNames;
import com.codetopic.utils.Utils;
import com.codetopic.utils.data.getter.DataGetter;
import com.codetopic.utils.data.preferences.SharedPreferencesData;
import com.codetopic.utils.data.preferences.SharedPreferencesGetterAbs;

public final class TimingData extends SharedPreferencesData {

    public static final DataGetter<TimingData> getter = new TimingDataGetter();
    private static final String LOG_TAG = "TimingData";
    private static final int SAVE_VERSION = 0;
    private static TimingData mInstance = null;

    private TimingData(Context context) {
        super(context, PrefNames.FILE_NAME_TIMING_DATA, SAVE_VERSION);
    }

    static void initialize(Context context) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new TimingData(context);
        mInstance.init();
    }

    public boolean isFirstLoad() {
        int versionCode = Utils.getApplicationVersionCode(getContext());
        int lastVersionCode = getPreferences().getInt(PrefNames.LAST_LOAD_VERSION_CODE, -1);
        edit().putInt(PrefNames.LAST_LOAD_VERSION_CODE, versionCode).apply();
        return versionCode != lastVersionCode;
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

    private static class TimingDataGetter extends SharedPreferencesGetterAbs<TimingData> {

        @Override
        public TimingData get() {
            return mInstance;
        }

        @Override
        public Class<TimingData> getDataClass() {
            return TimingData.class;
        }
    }
}

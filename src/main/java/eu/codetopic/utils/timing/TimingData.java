package eu.codetopic.utils.timing;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.BuildConfig;
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.PreferencesGetterAbs;
import eu.codetopic.utils.data.preferences.SharedPreferencesData;

import static eu.codetopic.utils.PrefNames.ADD_LAST_BROADCAST_REQUEST_CODE;
import static eu.codetopic.utils.PrefNames.ADD_TIME_LAST_START;
import static eu.codetopic.utils.PrefNames.FILE_NAME_TIMING_DATA;
import static eu.codetopic.utils.PrefNames.LAST_LOAD_VERSION_CODE;
import static eu.codetopic.utils.PrefNames.WAS_LAST_NETWORK_RELOAD_CONNECTED;

@MainThread
public final class TimingData extends SharedPreferencesData {

    public static final DataGetter<TimingData> getter = new TimingDataGetter();
    private static final String LOG_TAG = "TimingData";
    private static final int SAVE_VERSION = 0;
    private static TimingData mInstance = null;

    private TimingData(Context context) {
        super(context, FILE_NAME_TIMING_DATA, SAVE_VERSION);
    }

    static void initialize(Context context) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new TimingData(context);
        mInstance.init();
    }

    boolean isFirstLoad() {
        if (BuildConfig.DEBUG)
            return true;//fixes reinstall of application without increasing version code

        int versionCode = AndroidUtils.getApplicationVersionCode(getContext());
        int lastVersionCode = getPreferences().getInt(LAST_LOAD_VERSION_CODE, -1);
        edit().putInt(LAST_LOAD_VERSION_CODE, versionCode).apply();
        return versionCode != lastVersionCode;
    }

    public boolean getWasLastNetworkReloadConnected() {
        return getPreferences().getBoolean(WAS_LAST_NETWORK_RELOAD_CONNECTED, false);
    }

    void setWasLastNetworkReloadConnected(boolean wasConnection) {
        edit().putBoolean(WAS_LAST_NETWORK_RELOAD_CONNECTED, wasConnection).apply();
    }

    void clear(@NonNull Class clazz) {
        edit().remove(clazz.getName() + ADD_TIME_LAST_START)
                .remove(clazz.getName() + ADD_LAST_BROADCAST_REQUEST_CODE).apply();
    }

    public long getLastExecuteTime(@NonNull Class clazz) {
        return getPreferences().getLong(clazz.getName() + ADD_TIME_LAST_START, -1L);
    }

    void setLastExecuteTime(@NonNull Class clazz, long lastExecuteTime) {
        edit().putLong(clazz.getName() + ADD_TIME_LAST_START, lastExecuteTime).apply();
    }

    public int getLastRequestCode(@NonNull Class clazz) {
        return getPreferences().getInt(clazz.getName() + ADD_LAST_BROADCAST_REQUEST_CODE, -1);
    }

    void setLastRequestCode(@NonNull Class clazz, int lastRequestCode) {
        edit().putInt(clazz.getName() + ADD_LAST_BROADCAST_REQUEST_CODE, lastRequestCode).apply();
    }

    private static class TimingDataGetter extends PreferencesGetterAbs<TimingData> {

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

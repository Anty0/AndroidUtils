/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.timing;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.BuildConfig;
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.VersionedPreferencesData;
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider;
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs;

import static eu.codetopic.utils.PrefNames.ADD_LAST_BROADCAST_REQUEST_CODE;
import static eu.codetopic.utils.PrefNames.ADD_TIME_LAST_START;
import static eu.codetopic.utils.PrefNames.DEBUG_LOG_LINES;
import static eu.codetopic.utils.PrefNames.FILE_NAME_TIMING_DATA;
import static eu.codetopic.utils.PrefNames.LAST_LOAD_VERSION_CODE;
import static eu.codetopic.utils.PrefNames.WAS_LAST_NETWORK_RELOAD_CONNECTED;

@MainThread
public final class TimingData extends VersionedPreferencesData<SharedPreferences> {

    public static final DataGetter<TimingData> getter = new TimingDataGetter();

    private static final String LOG_TAG = "TimingData";
    private static final int SAVE_VERSION = 0;

    private static TimingData mInstance = null;

    private TimingData(Context context) {
        super(context, new BasicSharedPreferencesProvider(context,
                FILE_NAME_TIMING_DATA, Context.MODE_PRIVATE), SAVE_VERSION);
    }

    static void initialize(Context context) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new TimingData(context);
        mInstance.init();
    }

    boolean isFirstLoad() {
        if (BuildConfig.DEBUG)
            return true; // fixes reinstall of application without increasing version code

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

    public String getDebugLogJson() {
        return getPreferences().getString(DEBUG_LOG_LINES, "");
    }

    void addDebugLogLine(String text) {
        addDebugLogLine(System.currentTimeMillis(), text);
    }

    void addDebugLogLine(long time, String text) {
        try {
            JSONArray line = new JSONArray();
            line.put(time);
            line.put(text);

            String logLinesJson = getPreferences().getString(DEBUG_LOG_LINES, null);
            JSONArray logLinesArray = logLinesJson == null ? new JSONArray() : new JSONArray(logLinesJson);
            logLinesArray.put(line);

            edit().putString(DEBUG_LOG_LINES, logLinesArray.toString());
        } catch (JSONException e) {
            Log.w(LOG_TAG, e);
        }
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

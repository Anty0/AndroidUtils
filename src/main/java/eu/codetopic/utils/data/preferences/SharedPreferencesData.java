package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public abstract class SharedPreferencesData extends PreferencesData {

    private static final String LOG_TAG = "SharedPreferencesData";
    private final int mPrefOperatingMode;

    public SharedPreferencesData(Context context, @Nullable String fileName, int saveVersion) {
        this(context, fileName, Context.MODE_PRIVATE, saveVersion);
    }

    public SharedPreferencesData(Context context, @Nullable String fileName, int prefOperatingMode, int saveVersion) {
        super(context, fileName, saveVersion);
        mPrefOperatingMode = prefOperatingMode;
    }

    protected synchronized SharedPreferences createSharedPreferences() {
        String filename = getFileName();
        if (filename == null) return PreferenceManager.getDefaultSharedPreferences(getContext());
        return getContext().getSharedPreferences(filename, mPrefOperatingMode);
    }

    public int getPrefOperatingMode() {
        return mPrefOperatingMode;
    }
}

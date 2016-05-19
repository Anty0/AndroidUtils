package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import eu.codetopic.utils.PrefNames;

public abstract class SharedPreferencesData {

    public static final String EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY";
    protected static final Gson GSON = new Gson();
    private static final String LOG_TAG = "SharedPreferencesData";
    private final Context mContext;
    private final String mFileName;
    private final int mSaveVersion;
    private final int mPrefOperatingMode;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    onChanged(key);
                }
            };
    private SharedPreferences mPreferences;
    private boolean mCreated = false;
    private boolean mDestroyed = false;


    public SharedPreferencesData(Context context, String fileName, int saveVersion) {
        this(context, fileName, Context.MODE_PRIVATE, saveVersion);
    }

    public SharedPreferencesData(Context context, String fileName, int prefOperatingMode, int saveVersion) {
        mContext = context.getApplicationContext();
        mFileName = fileName;
        mPrefOperatingMode = prefOperatingMode;
        mSaveVersion = saveVersion;
    }

    public static String getBroadcastActionChanged(SharedPreferencesData data) {
        return SharedPreferencesData.class.getName() + ".PREFERENCES_CHANGED." + data.getFileName();
    }

    private Intent generateIntentActionChanged(String changedKey) {
        return new Intent(getBroadcastActionChanged(this))
                .putExtra(EXTRA_CHANGED_DATA_KEY, changedKey);
    }

    public final synchronized void init() {
        if (mCreated) throw new IllegalStateException(LOG_TAG + " is still initialized");
        if (mDestroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mCreated = true;
        onCreate();
    }

    public final synchronized boolean isCreated() {
        return mCreated;
    }

    public final synchronized boolean isDestroyed() {
        return mDestroyed;
    }

    protected synchronized void onCreate() {
        mPreferences = createSharedPreferences();
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        checkUpgrade(mSaveVersion);
    }

    protected synchronized SharedPreferences createSharedPreferences() {
        return mContext.getSharedPreferences(mFileName, mPrefOperatingMode);
    }

    private synchronized void checkUpgrade(int saveVersion) {
        int actualVersion = getPreferences().getInt(PrefNames.DATA_SAVE_VERSION, -1);
        if (actualVersion != saveVersion) {
            SharedPreferences.Editor editor = edit();
            onUpgrade(editor, actualVersion, saveVersion);
            editor.putInt(PrefNames.DATA_SAVE_VERSION, saveVersion).apply();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public String getFileName() {
        return mFileName;
    }

    public int getPrefOperatingMode() {
        return mPrefOperatingMode;
    }

    protected synchronized SharedPreferences getPreferences() {
        return mPreferences;
    }

    protected synchronized SharedPreferences.Editor edit() {
        return mPreferences.edit();
    }

    protected synchronized void onUpgrade(SharedPreferences.Editor editor, int from, int to) {
        editor.clear();
    }

    protected synchronized void onChanged(String key) {
        mContext.sendBroadcast(generateIntentActionChanged(key));
    }

    protected synchronized void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        mPreferences = null;
    }

    public final synchronized void destroy() {
        if (!mCreated) throw new IllegalStateException(LOG_TAG + " is not initialized");
        if (mDestroyed) throw new IllegalStateException(LOG_TAG + " is still destroyed");
        mDestroyed = true;
        mCreated = false;
        onDestroy();
    }

    @Override
    protected void finalize() throws Throwable {
        if (mCreated && !mDestroyed) destroy();
        super.finalize();
    }

}

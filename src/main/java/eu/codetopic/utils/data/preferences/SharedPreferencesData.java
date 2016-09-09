package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.getter.DataGetter;

public abstract class SharedPreferencesData {

    public static final String EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY";
    protected static final Gson GSON = new Gson();
    private static final String LOG_TAG = "SharedPreferencesData";
    private final Context mContext;
    @Nullable private final String mFileName;
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


    public SharedPreferencesData(Context context, @Nullable String fileName, int saveVersion) {
        this(context, fileName, Context.MODE_PRIVATE, saveVersion);
    }

    public SharedPreferencesData(Context context, @Nullable String fileName, int prefOperatingMode, int saveVersion) {
        mContext = context.getApplicationContext();
        mFileName = fileName;
        mPrefOperatingMode = prefOperatingMode;
        mSaveVersion = saveVersion;
    }

    public static String getBroadcastActionChanged(@NonNull DataGetter<? extends SharedPreferencesData> dataGetter) {
        return getBroadcastActionChanged(dataGetter.get());
    }

    public static String getBroadcastActionChanged(@NonNull SharedPreferencesData data) {
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
        try {
            onCreate();
        } catch (Throwable t) {
            destroy();
            throw t;
        }
    }

    public final synchronized boolean isCreated() {
        return mCreated;
    }

    public final synchronized boolean isDestroyed() {
        return mDestroyed;
    }

    @CallSuper
    protected synchronized void onCreate() {
        mPreferences = createSharedPreferences();
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        checkUpgrade(mSaveVersion);
    }

    protected synchronized SharedPreferences createSharedPreferences() {
        if (mFileName == null) return PreferenceManager.getDefaultSharedPreferences(mContext);
        return mContext.getSharedPreferences(mFileName, mPrefOperatingMode);
    }

    private synchronized void checkUpgrade(int saveVersion) {
        int actualVersion = getPreferences().getInt(PrefNames.DATA_SAVE_VERSION, -1);
        if (actualVersion != saveVersion) {
            SharedPreferences.Editor editor = edit();
            if (saveVersion > actualVersion) onUpgrade(editor, actualVersion, saveVersion);
            else onDowngrade(editor, actualVersion, saveVersion);
            editor.putInt(PrefNames.DATA_SAVE_VERSION, saveVersion).apply();
        }
    }

    public Context getContext() {
        return mContext;
    }

    @Nullable
    public String getFileName() {
        return mFileName;
    }

    public int getPrefOperatingMode() {
        return mPrefOperatingMode;
    }

    protected synchronized SharedPreferences getPreferences() {
        if (!mCreated) throw new IllegalStateException(LOG_TAG + " is not initialized");
        if (mDestroyed) throw new IllegalStateException(LOG_TAG + " is still destroyed");
        return mPreferences;
    }

    protected synchronized SharedPreferences.Editor edit() {
        return getPreferences().edit();
    }

    protected synchronized void onUpgrade(SharedPreferences.Editor editor, int from, int to) {
        editor.clear();
    }

    protected synchronized void onDowngrade(SharedPreferences.Editor editor, int from, int to) {
        throw new UnsupportedOperationException("Version code cannot be downgraded");
    }

    @CallSuper
    protected synchronized void onChanged(String key) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(generateIntentActionChanged(key));
    }

    @CallSuper
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

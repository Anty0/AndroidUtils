package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.getter.DataGetter;

public abstract class PreferencesData {

    public static final String EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY";
    protected static final Gson GSON = new Gson();
    private static final String LOG_TAG = "PreferencesData";
    private final Context mContext;
    @Nullable private final String mFileName;
    private final int mSaveVersion;
    private final OnSharedPreferenceChangeListener mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            onChanged(key);
        }
    };
    private SharedPreferences mPreferences;
    private boolean mCreated = false;
    private boolean mDestroyed = false;

    public PreferencesData(Context context, @Nullable String fileName, int saveVersion) {
        mContext = context.getApplicationContext();
        mFileName = fileName;
        mSaveVersion = saveVersion;
    }

    public static String getBroadcastActionChanged(@NonNull DataGetter<? extends PreferencesData> dataGetter) {
        return getBroadcastActionChanged(dataGetter.get());
    }

    public static String getBroadcastActionChanged(@NonNull PreferencesData data) {
        String fileName = data.getFileName();
        return SharedPreferencesData.class.getName() + ".PREFERENCES_CHANGED" + (fileName == null ? "" : "." + fileName);
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

    protected abstract SharedPreferences createSharedPreferences();

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

    @Override
    public String toString() {
        return "SharedPreferencesData{" +
                "mFileName='" + mFileName + '\'' +
                ", mSaveVersion=" + mSaveVersion +
                ", mPreferences=" + mPreferences +
                ", mCreated=" + mCreated +
                ", mDestroyed=" + mDestroyed +
                '}';
    }
}

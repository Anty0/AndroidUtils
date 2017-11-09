package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.preferences.provider.SharedPreferencesProvider;
import eu.codetopic.utils.data.preferences.support.NoApplyPreferencesEditor;

public abstract class PreferencesData<SP extends SharedPreferences> {

    private static final String LOG_TAG = "PreferencesData";

    private static final String ACTION_DATA_CHANGED_BASE =
            "eu.codetopic.utils.data.preferences.PreferencesData.PREFERENCES_CHANGED.$1%s";
    public static final String EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY";

    private final OnSharedPreferenceChangeListener mPreferenceChangeListener = (sharedPreferences, key) -> onChanged(key);

    private final Context mContext;
    private final SharedPreferencesProvider<SP> mPreferencesProvider;
    private final int mSaveVersion;

    private boolean mCreated = false;
    private boolean mDestroyed = false;

    public PreferencesData(Context context, @NonNull SharedPreferencesProvider<SP> preferencesProvider, int saveVersion) {
        mContext = context.getApplicationContext();
        mPreferencesProvider = preferencesProvider;
        mSaveVersion = saveVersion;
    }

    private static String getBroadcastActionChanged(@NonNull PreferencesData data) {
        String name = data.getName();
        return String.format(ACTION_DATA_CHANGED_BASE, (name == null ? "default" : name));
    }

    public final String getBroadcastActionChanged() {
        return getBroadcastActionChanged(this);
    }

    private Intent generateIntentActionChanged(String changedKey) {
        return new Intent(this.getBroadcastActionChanged())
                .putExtra(EXTRA_CHANGED_DATA_KEY, changedKey)
                .putExtras(getAdditionalDataChangedExtras(changedKey));
    }

    protected Bundle getAdditionalDataChangedExtras(String changedKey) {
        return new Bundle();
    }

    public Context getContext() {
        return mContext;
    }

    @Nullable
    public String getName() {
        return mPreferencesProvider.getName();
    }

    protected synchronized SharedPreferences getPreferences() {
        if (!mCreated) throw new IllegalStateException(LOG_TAG + " is not initialized");
        if (mDestroyed) throw new IllegalStateException(LOG_TAG + " is still destroyed");
        return mPreferencesProvider.getSharedPreferences();
    }

    protected synchronized SharedPreferences.Editor edit() {
        return getPreferences().edit();
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
        getPreferences().registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        checkUpgrade(mSaveVersion);
    }

    private synchronized void checkUpgrade(int saveVersion) {
        int actualVersion = getPreferences().getInt(PrefNames.DATA_SAVE_VERSION, -1);
        if (actualVersion != saveVersion) {
            SharedPreferences.Editor editor = edit();

            NoApplyPreferencesEditor noApplyEditor = new  NoApplyPreferencesEditor(editor,
                    "Don't call methods editor.apply() or editor.commit() " +
                            "during onUpgrade() or onDowngrade(). Changes will be saved later automatically.");
            if (saveVersion > actualVersion) onUpgrade(noApplyEditor, actualVersion, saveVersion);
            else onDowngrade(noApplyEditor, actualVersion, saveVersion);

            editor.putInt(PrefNames.DATA_SAVE_VERSION, saveVersion).apply();
        }
    }

    protected synchronized void onUpgrade(SharedPreferences.Editor editor, int from, int to) {
        // Default implementation will just throw all data away.
        editor.clear();
    }

    protected synchronized void onDowngrade(SharedPreferences.Editor editor, int from, int to) {
        // Default implementation should not support downgrading, so we will just throw exception.
        throw new UnsupportedOperationException("Version code cannot be downgraded");
    }

    @CallSuper
    protected synchronized void onChanged(String key) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(generateIntentActionChanged(key));
    }

    @CallSuper
    protected synchronized void onDestroy() {
        getPreferences().unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
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
        return "PreferencesData{" +
                ", mSaveVersion=" + mSaveVersion +
                ", mPreferencesProvider=" + mPreferencesProvider +
                ", mCreated=" + mCreated +
                ", mDestroyed=" + mDestroyed +
                '}';
    }
}

package eu.codetopic.utils.module.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import eu.codetopic.utils.PrefNames;

/**
 * Created by anty on 24.2.16.
 *
 * @author anty
 */
public class ModuleData {

    private final Context mContext;
    private final String mFileName;
    private final int mSaveVersion;
    private final int mPrefOperatingMode;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    mContext.sendBroadcast(new Intent(ModuleDataManager
                            .getBroadcastActionChanged(ModuleData.this)));
                }
            };
    private SharedPreferences mPreferences;


    public ModuleData(Context context, String fileName, int saveVersion) {
        this(context, fileName, Context.MODE_PRIVATE, saveVersion);
    }

    public ModuleData(Context context, String fileName, int prefOperatingMode, int saveVersion) {
        mContext = context.getApplicationContext();
        mFileName = fileName;
        mPrefOperatingMode = prefOperatingMode;
        mSaveVersion = saveVersion;
    }

    public void onCreate() {
        mPreferences = createSharedPreferences();
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        checkUpgrade(mSaveVersion);
    }

    protected SharedPreferences createSharedPreferences() {
        return mContext.getSharedPreferences(mFileName, mPrefOperatingMode);
    }

    private void checkUpgrade(int saveVersion) {
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

    protected SharedPreferences getPreferences() {
        return mPreferences;
    }

    protected SharedPreferences.Editor edit() {
        return mPreferences.edit();
    }

    protected void onUpgrade(SharedPreferences.Editor editor, int from, int to) {
        editor.clear();
    }

    public void onDestroy() throws Throwable {
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        mPreferences = null;
    }

    @Override
    protected void finalize() throws Throwable {
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        super.finalize();
    }
}

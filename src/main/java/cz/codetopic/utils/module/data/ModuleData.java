package cz.codetopic.utils.module.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

import cz.codetopic.utils.PrefNames;

/**
 * Created by anty on 24.2.16.
 *
 * @author anty
 */
public class ModuleData {

    private final Context mContext;
    private final String mFileName;
    private final SharedPreferences mPreferences;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    mContext.sendBroadcast(new Intent(ModuleDataManager
                            .getBroadcastActionChanged(ModuleData.this)));
                }
            };


    public ModuleData(Context context, String fileName, boolean secured, int saveVersion) {
        this(context, fileName, Context.MODE_PRIVATE, secured, saveVersion);
    }

    public ModuleData(Context context, String fileName, int prefOperatingMode, boolean secured, int saveVersion) {
        mContext = context.getApplicationContext();
        mFileName = fileName;
        mPreferences = secured ? new SecurePreferences(mContext, "", mFileName) :
                context.getSharedPreferences(fileName, prefOperatingMode);
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        checkUpgrade(saveVersion);
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

    protected SharedPreferences getPreferences() {
        return mPreferences;
    }

    protected SharedPreferences.Editor edit() {
        return mPreferences.edit();
    }

    protected void onUpgrade(SharedPreferences.Editor editor, int from, int to) {
        editor.clear();
    }

    public void close() throws Throwable {
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    protected void finalize() throws Throwable {
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        super.finalize();
    }
}

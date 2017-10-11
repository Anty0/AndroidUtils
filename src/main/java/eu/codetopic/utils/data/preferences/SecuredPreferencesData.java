package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.securepreferences.SecurePreferences;

import eu.codetopic.java.utils.log.Log;

public abstract class SecuredPreferencesData extends PreferencesData {

    private static final String LOG_TAG = "SecureModuleData";
    private static final String DEFAULT_PASSWORD = "TheBestDefaultPasswordEver";

    private final boolean mClearOnFail;
    private final String mPassword;

    public SecuredPreferencesData(Context context, @NonNull String fileName, boolean clearOnFail, int saveVersion) {
        this(context, fileName, DEFAULT_PASSWORD, clearOnFail, saveVersion);
    }

    public SecuredPreferencesData(Context context, @NonNull String fileName, String password, boolean clearOnFail, int saveVersion) {
        super(context, fileName, saveVersion);
        mClearOnFail = clearOnFail;
        mPassword = password;
    }

    @Override
    protected SharedPreferences createSharedPreferences() {
        try {
            return new SecurePreferences(getContext(), mPassword, getFileName());
        } catch (Throwable t) {
            if (mClearOnFail) {
                Log.e(LOG_TAG, "createSharedPreferences: failed to create SecurePreferences, data will be cleared", t);
                SharedPreferences pref = getContext().getSharedPreferences(getFileName(), Context.MODE_PRIVATE);
                if (!pref.getAll().isEmpty()) {
                    pref.edit().clear().apply();
                    return createSharedPreferences();
                }
                throw t;
            }
            Log.e(LOG_TAG, "createSharedPreferences: failed to create SecurePreferences", t);
            throw t;
        }
    }
}

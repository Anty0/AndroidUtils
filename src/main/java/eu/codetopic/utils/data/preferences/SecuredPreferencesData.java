package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.securepreferences.SecurePreferences;

import eu.codetopic.java.utils.log.Log;

public abstract class SecuredPreferencesData extends PreferencesData {

    private static final String LOG_TAG = "SecureModuleData";
    private static final String DEFAULT_PASSWORD = "TheBestDefaultPasswordEver";

    private final String mPassword;

    public SecuredPreferencesData(Context context, @NonNull String fileName, int saveVersion) {
        this(context, fileName, DEFAULT_PASSWORD, saveVersion);
    }

    public SecuredPreferencesData(Context context, @NonNull String fileName, String password, int saveVersion) {
        super(context, fileName, saveVersion);
        mPassword = password;
    }

    @Override
    protected SharedPreferences createSharedPreferences() {
        try {
            return new SecurePreferences(getContext(), mPassword, getFileName());
        } catch (Throwable t) {
            Log.e(LOG_TAG, "createSharedPreferences: data cleared", t);
            getContext().getSharedPreferences(getFileName(),
                    Context.MODE_PRIVATE).edit().clear().apply();
            return createSharedPreferences();
        }
    }
}

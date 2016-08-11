package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

public abstract class SecuredPreferencesData extends SharedPreferencesData {

    private static final String LOG_TAG = "SecureModuleData";
    private static final String DEFAULT_PASSWORD = "TheBestDefaultPasswordEver";

    private final String mPassword;

    public SecuredPreferencesData(Context context, String fileName, int saveVersion) {
        this(context, fileName, DEFAULT_PASSWORD, saveVersion);
    }

    public SecuredPreferencesData(Context context, String fileName, String password, int saveVersion) {
        super(context, fileName, Context.MODE_PRIVATE, saveVersion);
        mPassword = password;
    }

    @Override
    protected SharedPreferences createSharedPreferences() {
        return new SecurePreferences(getContext(), mPassword, getFileName());
    }
}

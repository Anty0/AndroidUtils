package eu.codetopic.utils.module.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public class SecureModuleData extends ModuleData {

    private static final String LOG_TAG = "SecureModuleData";
    private static final String DEFAULT_PASSWORD = "TheBestDefaultPasswordEver";

    private final String mPassword;

    public SecureModuleData(Context context, String fileName, int saveVersion) {
        this(context, fileName, DEFAULT_PASSWORD, saveVersion);
    }

    public SecureModuleData(Context context, String fileName, String password, int saveVersion) {
        super(context, fileName, Context.MODE_PRIVATE, saveVersion);
        mPassword = password;
    }

    @Override
    protected SharedPreferences createSharedPreferences() {
        return new SecurePreferences(getContext(), mPassword, getFileName());
    }
}

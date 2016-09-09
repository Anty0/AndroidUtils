package eu.codetopic.utils.data.preferences;

import android.content.Context;

import eu.codetopic.utils.log.Log;

import static eu.codetopic.utils.PrefNames.LOGGED_IN;
import static eu.codetopic.utils.PrefNames.PASSWORD;
import static eu.codetopic.utils.PrefNames.USERNAME;

public class LoginData extends SecuredPreferencesData {

    private static final String LOG_TAG = "LoginData";

    public LoginData(Context context, String fileName, int saveVersion) {
        super(context, fileName, saveVersion);
    }

    public synchronized void login(String username, String password) {
        Log.d(LOG_TAG, "login fileName: " + getFileName());
        edit().putString(USERNAME, username)
                .putString(PASSWORD, password)
                .putBoolean(LOGGED_IN, true)
                .apply();
    }

    public synchronized void logout() {
        Log.d(LOG_TAG, "logout fileName: " + getFileName());
        edit().putBoolean(LOGGED_IN, false).remove(PASSWORD).apply();
    }

    public synchronized boolean isLoggedIn() {
        Log.d(LOG_TAG, "isLoggedIn fileName: " + getFileName());
        return getPreferences().getBoolean(LOGGED_IN, false);
    }

    public synchronized String getUsername() {
        Log.d(LOG_TAG, "getUsername fileName: " + getFileName());
        return getPreferences().getString(USERNAME, "");
    }

    public synchronized String getPassword() {
        Log.d(LOG_TAG, "getPassword fileName: " + getFileName());
        return getPreferences().getString(PASSWORD, "");
    }
}

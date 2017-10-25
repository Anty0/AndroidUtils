package eu.codetopic.utils.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import eu.codetopic.java.utils.log.Log;

import static eu.codetopic.utils.PrefNames.LOGGED_IN;
import static eu.codetopic.utils.PrefNames.PASSWORD;
import static eu.codetopic.utils.PrefNames.USERNAME;

public class LoginData extends SecuredPreferencesData {

    private static final String LOG_TAG = "LoginData";

    public static final String DEFAULT_ID = "default";

    public LoginData(Context context, String fileName, int saveVersion) {
        super(context, fileName, true, saveVersion);
    }

    private void log(String methodName, String id) {
        Log.d(LOG_TAG, String.format("%s: { fileName: %s, id: %s}", methodName, getFileName(), id));
    }

    protected String formatKey(String id, String key) {
        return String.format("ID{%s}-%s", id, key);
    }

    public synchronized void login(String username, String password) {
        login(DEFAULT_ID, username, password);
    }

    public synchronized void login(String id, String username, String password) {
        log("login", id);
        edit().putString(formatKey(id, USERNAME), username)
                .putString(formatKey(id, PASSWORD), password)
                .putBoolean(formatKey(id, LOGGED_IN), true)
                .apply();
    }

    public synchronized void logout() {
        logout(DEFAULT_ID);
    }

    public synchronized void logout(String id) {
        log("logout", id);
        edit().putBoolean(formatKey(id, LOGGED_IN), false)
                .remove(formatKey(id, PASSWORD))
                .apply();
    }

    public synchronized boolean isLoggedIn() {
        return isLoggedIn(DEFAULT_ID);
    }

    public synchronized boolean isLoggedIn(String id) {
        log("isLoggedIn", id);
        return getPreferences().getBoolean(formatKey(id, LOGGED_IN), false);
    }

    public synchronized String getUsername() {
        return getUsername(DEFAULT_ID);
    }

    public synchronized String getUsername(String id) {
        log("getUsername", id);
        return getPreferences().getString(USERNAME, null);
    }

    public synchronized String getPassword() {
        return getPassword(DEFAULT_ID);
    }

    public synchronized String getPassword(String id) {
        log("getPassword", id);
        return getPreferences().getString(PASSWORD, null);
    }


    public synchronized void clearData() {
        clearData(DEFAULT_ID);
    }

    public synchronized void clearData(String id) {
        log("clearData", id);
        edit().remove(formatKey(id, USERNAME))
                .remove(formatKey(id, PASSWORD))
                .remove(formatKey(id, LOGGED_IN))
                .apply();
    }

    public synchronized void changeId(String oldId, String newId) {
        log("changeId", String.format("%s->%s", oldId, newId));
        SharedPreferences prefs = getPreferences();
        edit().putString(formatKey(newId, USERNAME), prefs.getString(formatKey(oldId, USERNAME), null))
                .putString(formatKey(newId, PASSWORD), prefs.getString(formatKey(oldId, PASSWORD), null))
                .putBoolean(formatKey(newId, LOGGED_IN), prefs.getBoolean(formatKey(oldId, LOGGED_IN), false))
                .remove(formatKey(oldId, USERNAME))
                .remove(formatKey(oldId, PASSWORD))
                .remove(formatKey(oldId, LOGGED_IN))
                .apply();
    }
}

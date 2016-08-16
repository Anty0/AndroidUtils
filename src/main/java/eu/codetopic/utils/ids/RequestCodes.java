package eu.codetopic.utils.ids;

import android.content.Context;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.preferences.SharedPreferencesData;

public class RequestCodes extends SharedPreferencesData {

    private static final String LOG_TAG = "RequestCodes";
    private static final int SAVE_VERSION = 0;

    private static RequestCodes mInstance;

    private RequestCodes(Context context) {
        super(context, PrefNames.FILE_NAME_REQUEST_CODES, SAVE_VERSION);
    }

    public static void initialize(Context context) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new RequestCodes(context);
        mInstance.init();
    }

    public static int requestCode() {
        return mInstance.nextRequestCode();
    }

    private int getLastRequestCode() {
        return getPreferences().getInt(PrefNames.LAST_REQUEST_CODE, 0);
    }

    private int nextRequestCode() {
        int lastRequestCode = getLastRequestCode();
        int nextRequestCode = lastRequestCode >= Integer.MAX_VALUE ? 0 : lastRequestCode + 1;
        edit().putInt(PrefNames.LAST_REQUEST_CODE, nextRequestCode).apply();
        return nextRequestCode;
    }

}

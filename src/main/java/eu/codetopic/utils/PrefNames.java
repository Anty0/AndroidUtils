package eu.codetopic.utils;

public final class PrefNames {

    // SharedPreferencesData
    public static final String DATA_SAVE_VERSION = "DATA_SAVE_VERSION";
    // LoginDataExtension
    public static final String USERNAME = "LOGIN";
    public static final String PASSWORD = "PASSWORD";
    public static final String LOGGED_IN = "LOGGED_IN";
    // Identifiers
    public static final String FILE_NAME_IDENTIFIERS = "IdentifiersData";
    public static final String ADD_LAST_ID = "LAST_";
    public static final String ID_TYPE_REQUEST_CODE = "REQUEST_CODE";
    public static final String ID_TYPE_NOTIFICATION_ID = "NOTIFICATION_ID";
    // TimingData
    public static final String FILE_NAME_TIMING_DATA = "TimingData";
    public static final String LAST_LOAD_VERSION_CODE = "LAST_LOAD_VERSION_CODE";
    public static final String ADD_TIME_LAST_START = "_LAST_START";
    public static final String ADD_LAST_BROADCAST_REQUEST_CODE = "_LAST_REQUEST_CODE";
    public static final String WAS_LAST_NETWORK_RELOAD_CONNECTED = "WAS_CONNECTED";
    public static final String DEBUG_LOG_LINES = "DEBUG_LOG_LINES";
    // LocaleData
    public static final String FILE_NAME_LOCALE_DATA = "LocaleData";
    public static final String ACTUAL_LOCALE = "LOCALE";
    // DashboardData
    public static final String FILE_NAME_DASHBOARD_DATA = "DashboardData";
    public static final String ADD_ENABLED_STATE = "_ENABLED_STATE";

    private PrefNames() {
    }

}

package eu.codetopic.utils;

public final class PrefNames {

    //SharedPreferencesData
    public static final String DATA_SAVE_VERSION = "DATA_SAVE_VERSION";
    //LoginData
    public static final String LOGIN = "LOGIN";
    public static final String PASSWORD = "PASSWORD";
    public static final String LOGGED_IN = "LOGGED_IN";
    //UsedIdsData
    @Deprecated public static final String FILE_NAME_USED_IDS_DATA = "UsedIdsData";
    @Deprecated public static final String ADD_IDS_FOR_GROUP = "_USED_IDS";
    @Deprecated public static final String ALL_USED_IDS = "USED_IDS";
    @Deprecated public static final String LAST_NOTIFICATION_REQUEST_CODE = "LAST_NOTIFICATION_REQUEST_CODE";
    //NotificationsData
    public static final String FILE_NAME_NOTIFICATIONS_DATA = "NotificationsData";
    public static final String NOTIFICATIONS_CASES = "NOTIFICATIONS_CASES";
    //RequestCodes
    public static final String FILE_NAME_REQUEST_CODES = "RequestCodesData";
    public static final String LAST_REQUEST_CODE = "LAST_REQUEST_CODE";
    //TimingData
    public static final String FILE_NAME_TIMING_DATA = "TimingData";
    public static final String LAST_LOAD_VERSION_CODE = "LAST_LOAD_VERSION_CODE";
    public static final String ADD_TIME_LAST_START = "_LAST_START";
    public static final String ADD_LAST_BROADCAST_REQUEST_CODE = "_LAST_REQUEST_CODE";
    //LocaleData
    public static final String FILE_NAME_LOCALE_DATA = "LocaleData";
    public static final String ACTUAL_LOCALE = "LOCALE";
    //DashboardData
    public static final String FILE_NAME_DASHBOARD_DATA = "DashboardData";
    public static final String ADD_ENABLED_STATE = "_ENABLED_STATE";

    private PrefNames() {
    }

}

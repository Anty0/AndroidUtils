/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

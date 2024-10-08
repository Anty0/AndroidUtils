/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    public static final String LAST_IDENTIFIER = "LAST_IDENTIFIER";
    public static final String ID_TYPE_REQUEST_CODE = "REQUEST_CODE";
    public static final String ID_TYPE_NOTIFICATION_ID = "NOTIFICATION_ID";
    // NotifyData
    public static final String FILE_NAME_NOTIFY_DATA = "NotifyData";
    public static final String NOTIFICATIONS_MAP = "NOTIFICATIONS_MAP";
    public static final String CHANNELS_ENABLE_MAP = "CHANNELS_ENABLE_MAP";
    public static final String BROADCAST_REJECTED_COUNTER = "BROADCAST_REJECTED_COUNTER";

    private PrefNames() {
    }

}

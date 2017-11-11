/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ids;

import android.content.Context;
import android.content.SharedPreferences;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.data.preferences.PreferencesData;
import eu.codetopic.utils.data.preferences.VersionedPreferencesData;
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider;

import static eu.codetopic.utils.PrefNames.ADD_LAST_ID;
import static eu.codetopic.utils.PrefNames.FILE_NAME_IDENTIFIERS;
import static eu.codetopic.utils.PrefNames.ID_TYPE_NOTIFICATION_ID;
import static eu.codetopic.utils.PrefNames.ID_TYPE_REQUEST_CODE;

public final class Identifiers extends VersionedPreferencesData<SharedPreferences> {

    public static final Type TYPE_REQUEST_CODE = new Type(ID_TYPE_REQUEST_CODE);
    public static final Type TYPE_NOTIFICATION_ID = new Type(ID_TYPE_NOTIFICATION_ID);

    private static final String LOG_TAG = "Identifiers";

    private static final int SAVE_VERSION = 0;

    private static Identifiers mInstance;

    private Identifiers(Context context) {
        super(context, new BasicSharedPreferencesProvider(context,
                FILE_NAME_IDENTIFIERS, Context.MODE_PRIVATE), SAVE_VERSION);
    }

    public static void initialize(Context context) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new Identifiers(context);
        mInstance.init();
    }

    public static int next(Type type) {
        return mInstance.getNext(type);
    }

    private int getLast(Type type) {
        return getPreferences().getInt(type.getSettingsName(), type.getMin());
    }

    private int getNext(Type type) {
        int next = type.getNext(getLast(type));
        edit().putInt(type.getSettingsName(), next).apply();
        Log.d(LOG_TAG, "Returning next identifier of type "
                + type.getName() + ", returned id is " + next);
        return next;
    }

    public static final class Type {

        private final String name;
        private final int min, max;

        public Type(String name) {
            this(name, 1, Integer.MAX_VALUE);
        }

        public Type(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
            if (min >= max) throw new IllegalArgumentException(
                    "Min cannot be same of higher then Max. " + this);
        }

        public String getName() {
            return name;
        }

        public String getSettingsName() {
            return ADD_LAST_ID + name;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public int getNext(int last) {
            return last >= max - 1 ? min : last + 1;
        }

        @Override
        public String toString() {
            return "Type{" +
                    "name='" + name + '\'' +
                    ", min=" + min +
                    ", max=" + max +
                    '}';
        }
    }

}

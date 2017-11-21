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

package eu.codetopic.utils.ui.locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.preferences.PreferencesData;
import eu.codetopic.utils.data.preferences.VersionedPreferencesData;
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider;

public final class LocaleData extends VersionedPreferencesData<SharedPreferences> {

    private static final int SAVE_VERSION = 0;

    public LocaleData(Context context) {
        super(context, new BasicSharedPreferencesProvider(context,
                PrefNames.FILE_NAME_LOCALE_DATA, Context.MODE_PRIVATE), SAVE_VERSION);
    }

    public String getActualLanguage() {
        return getPreferences().getString(PrefNames.ACTUAL_LOCALE, "unknown");// TODO: 16.6.16 find way to detect actual language
    }

    @Nullable
    String getLanguage() {
        return getPreferences().getString(PrefNames.ACTUAL_LOCALE, null);
    }

    void setLanguage(String newLocaleId) {
        edit().putString(PrefNames.ACTUAL_LOCALE, newLocaleId).apply();
    }
}

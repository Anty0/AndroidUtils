package eu.codetopic.utils.ui.locale;

import android.content.Context;
import android.support.annotation.Nullable;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.preferences.PreferencesData;
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider;

public final class LocaleData extends PreferencesData {

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

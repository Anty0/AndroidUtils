package eu.codetopic.utils.locale;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

import eu.codetopic.utils.R;
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.getter.DataGetterNoBroadcastImpl;

/**
 * Created by anty on 25.3.16.
 *
 * @author anty
 */
public class LocaleManager {

    private static final String LOG_TAG = "LocaleManager";

    private static Context mContext = null;
    private static DataGetter<LocaleData> mLocaleDataGetter = null;

    /**
     * Must be called in Application.onCreate() if you want to use this class
     *
     * @param app        application
     * @param localeData DataGetter of LocaleData for saving changes
     */
    @Deprecated
    public static void initialize(@NonNull Application app, @NonNull final LocaleData localeData) {
        initialize(app, new DataGetterNoBroadcastImpl<LocaleData>() {
            @Override
            public LocaleData get() {
                return localeData;
            }

            @Override
            public Class<LocaleData> getDataClass() {
                return LocaleData.class;
            }
        });
    }

    /**
     * Must be called in Application.onCreate() if you want to use this class
     *
     * @param app              application
     * @param localeDataGetter DataGetter of LocaleData for saving changes
     */
    public static void initialize(@NonNull Application app, @NonNull DataGetter<LocaleData> localeDataGetter) {// TODO: 26.3.16 initialize in ApplicationBase
        if (mContext != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mContext = app.getBaseContext();
        mLocaleDataGetter = localeDataGetter;
        restoreLocale();
    }

    public static void showLanguageChangeDialog(Activity activity, String[] locales, @Nullable final Runnable onChange) {
        final RadioGroup radioGroup = new RadioGroup(activity);
        radioGroup.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0, localesLength = locales.length; i < localesLength; i++) {
            String lang = locales[i];
            RadioButton radioButton = new RadioButton(activity);
            radioButton.setText(new Locale(lang).getDisplayLanguage());
            radioButton.setTag(lang);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }

        RadioButton radioButton = (RadioButton) radioGroup.findViewWithTag(mLocaleDataGetter.get().getActualLanguage());
        if (radioButton != null) radioGroup.check(radioButton.getId());

        new AlertDialog.Builder(activity)// TODO: 10.4.16 use dialog activity
                .setTitle(R.string.dialog_title_select_language)
                .setView(radioGroup)
                .setPositiveButton(R.string.but_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setLocale((String) radioGroup.findViewById(radioGroup
                                .getCheckedRadioButtonId()).getTag());
                        if (onChange != null) onChange.run();
                    }
                })
                .setCancelable(true)
                .setNeutralButton(R.string.but_cancel, null)
                .show();
    }

    public static void setLocale(String language) {
        applyLocale(language);
        mLocaleDataGetter.get().setLanguage(language);
    }

    private static void restoreLocale() {
        String lang = mLocaleDataGetter.get().getLanguage();
        if (lang != null) applyLocale(lang);
    }

    private static void applyLocale(String language) {
        Resources res = mContext.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language);
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

}

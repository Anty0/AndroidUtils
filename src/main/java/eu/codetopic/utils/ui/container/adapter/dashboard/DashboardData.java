package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.Context;
import android.content.SharedPreferences;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider;
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs;
import eu.codetopic.utils.data.preferences.PreferencesData;

public class DashboardData extends PreferencesData {

    public static final DataGetter<DashboardData> getter = new DashboardDataGetter();

    private static final String LOG_TAG = "DashboardData";
    private static final int SAVE_VERSION = 0;
    private static DashboardData mInstance = null;

    private DashboardData(Context context) {
        super(context, new BasicSharedPreferencesProvider(context,
                PrefNames.FILE_NAME_DASHBOARD_DATA, Context.MODE_PRIVATE), SAVE_VERSION);
    }

    public static void initialize(Context context) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new DashboardData(context);
        mInstance.init();
    }

    private String getKeyEnabledState(ItemInfo itemInfo) {
        return itemInfo.getSaveName() + PrefNames.ADD_ENABLED_STATE;
    }

    void saveItemState(ItemInfo itemInfo) {
        if (itemInfo.hasPersistentEnabledState())
            edit().putBoolean(getKeyEnabledState(itemInfo),
                    itemInfo.isEnabled()).apply();
    }

    void restoreItemState(ItemInfo itemInfo) {
        String key;
        SharedPreferences preferences = getPreferences();
        if (itemInfo.hasPersistentEnabledState() && preferences
                .contains(key = getKeyEnabledState(itemInfo))) {
            boolean enabled = preferences.getBoolean(key, itemInfo.getDefaultEnabledState());
            itemInfo.onRestoreEnabledState(enabled);
        }
    }

    private static class DashboardDataGetter extends PreferencesGetterAbs<DashboardData> {

        @Override
        public DashboardData get() {
            return mInstance;
        }

        @Override
        public Class<DashboardData> getDataClass() {
            return DashboardData.class;
        }
    }
}

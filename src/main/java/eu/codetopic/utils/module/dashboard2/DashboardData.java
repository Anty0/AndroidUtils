package eu.codetopic.utils.module.dashboard2;

import android.content.Context;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.module.data.ModuleData;
import eu.codetopic.utils.module.getter.ModuleDataGetter;
import eu.codetopic.utils.module.getter.ModuleDataGetterImpl;

/**
 * Created by anty on 2.5.16.
 *
 * @author anty
 */
public final class DashboardData extends ModuleData {

    public static final ModuleDataGetter<DashboardModule, DashboardData> getter =
            new ModuleDataGetterImpl<>(DashboardModule.class, DashboardData.class);

    private static final String LOG_TAG = "DashboardData";
    private static final int SAVE_VERSION = 0;

    public DashboardData(Context context) {
        super(context, PrefNames.FILE_NAME_DASHBOARD_DATA, SAVE_VERSION);
    }

    void saveItemDataState(DashboardItem.ItemData item) {
        String name = item.getSaveName();
        if (name == null) return;
        edit().putBoolean(name + PrefNames.ADD_DASHBOARD_ITEM_ENABLED,
                item.isUserEnabled()).apply();
    }

    void restoreItemDataState(DashboardItem.ItemData item) {
        String name = item.getSaveName();
        if (name == null) return;
        item.setUserEnabledInternal(getPreferences().getBoolean(name + PrefNames
                .ADD_DASHBOARD_ITEM_ENABLED, item.isUserEnabled()));
    }

    public boolean isShowDescription() {
        return getPreferences().getBoolean(PrefNames.SHOW_DESCRIPTION, true);
    }

    public void setShowDescription(boolean showDescription) {
        edit().putBoolean(PrefNames.SHOW_DESCRIPTION, showDescription).apply();
    }
}

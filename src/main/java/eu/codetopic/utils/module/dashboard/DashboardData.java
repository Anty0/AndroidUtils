package eu.codetopic.utils.module.dashboard;

import android.content.Context;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.module.data.ModuleData;

/**
 * Created by anty on 23.2.16.
 *
 * @author anty
 */
public class DashboardData extends ModuleData {

    public DashboardData(Context context, String fileName, int saveVersion) {
        super(context, fileName, false, saveVersion);
    }

    void saveDashboardItemEnabledState(DashboardItem item) {
        String name = item.getSaveName();
        if (name == null) return;
        edit().putBoolean(name + PrefNames.ADD_DASHBOARD_ITEM_ENABLED,
                item.isEnabled()).apply();
    }

    void restoreDashboardItemEnabledState(DashboardItem item) {
        String name = item.getSaveName();
        if (name == null) return;
        item.setEnabled(getPreferences().getBoolean(name + PrefNames
                .ADD_DASHBOARD_ITEM_ENABLED, item.isEnabled()));
    }

    public boolean isShowDescription() {
        return getPreferences().getBoolean(PrefNames.SHOW_DESCRIPTION, true);
    }

    public void setShowDescription(boolean showDescription) {
        edit().putBoolean(PrefNames.SHOW_DESCRIPTION, showDescription).apply();
    }
}

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

package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.Context;
import android.content.SharedPreferences;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.VersionedPreferencesData;
import eu.codetopic.utils.data.preferences.provider.BasicSharedPreferencesProvider;
import eu.codetopic.utils.data.preferences.support.PreferencesGetterAbs;
import eu.codetopic.utils.data.preferences.PreferencesData;

public final class DashboardData extends VersionedPreferencesData<SharedPreferences> {

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

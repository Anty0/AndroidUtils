package eu.codetopic.utils.module.data;

import android.content.Intent;

import eu.codetopic.utils.module.HashClassesManager;

/**
 * Created by anty on 16.2.16.
 *
 * @author anty
 */
public class ModuleDataManager extends HashClassesManager<ModuleData> {

    public static final String EXTRA_CHANGED_DATA_KEY = "CHANGED_DATA_KEY";

    public ModuleDataManager(ModuleData... moduleData) {
        super(moduleData);
        onCreate();
    }

    public static String getBroadcastActionChanged(ModuleData moduleData) {
        return ModuleDataManager.class.getName() + ".PREFERENCES_CHANGED." + moduleData.getFileName();
    }

    static Intent generateIntentActionChanged(ModuleData moduleData, String changedKey) {
        return new Intent(getBroadcastActionChanged(moduleData))
                .putExtra(ModuleDataManager.EXTRA_CHANGED_DATA_KEY, changedKey);
    }

    protected void onCreate() {
        for (ModuleData moduleData : get())
            moduleData.onCreate();
    }

    public void onDestroy() throws Throwable {
        for (ModuleData moduleData : get())
            moduleData.onDestroy();
    }

}

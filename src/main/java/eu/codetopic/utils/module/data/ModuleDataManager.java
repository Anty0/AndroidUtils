package eu.codetopic.utils.module.data;

import eu.codetopic.utils.module.HashClassesManager;

/**
 * Created by anty on 16.2.16.
 *
 * @author anty
 */
public class ModuleDataManager extends HashClassesManager<ModuleData> {

    public ModuleDataManager(ModuleData... moduleData) {
        super(moduleData);
        onCreate();
    }

    public static String getBroadcastActionChanged(ModuleData moduleData) {
        return ModuleDataManager.class.getName() + ".PREFERENCES_CHANGED." + moduleData.getFileName();
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

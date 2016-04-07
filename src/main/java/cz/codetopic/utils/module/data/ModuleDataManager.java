package cz.codetopic.utils.module.data;

import cz.codetopic.utils.module.HashClassesManager;

/**
 * Created by anty on 16.2.16.
 *
 * @author anty
 */
public class ModuleDataManager extends HashClassesManager<ModuleData> {

    public ModuleDataManager(ModuleData... moduleDatas) {
        super(moduleDatas);
    }

    public static String getBroadcastActionChanged(ModuleData moduleData) {
        return ModuleDataManager.class.getName() + ".PREFERENCES_CHANGED." + moduleData.getFileName();
    }

    public void close() throws Throwable {
        for (ModuleData moduleData : get())
            moduleData.close();
    }

}

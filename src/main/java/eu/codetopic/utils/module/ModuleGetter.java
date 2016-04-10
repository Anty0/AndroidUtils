package eu.codetopic.utils.module;

import java.io.Serializable;

/**
 * Created by anty on 18.3.16.
 *
 * @author anty
 */
public class ModuleGetter<MT extends Module> implements Serializable {

    private final Class<MT> mModuleClass;

    public ModuleGetter(Class<MT> moduleClass) {
        mModuleClass = moduleClass;
    }

    public Class<MT> getModuleClass() {
        return mModuleClass;
    }

    public MT getModule() {
        return ModulesManager.findModule(getModuleClass());
    }

}

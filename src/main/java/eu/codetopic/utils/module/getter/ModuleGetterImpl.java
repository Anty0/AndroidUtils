package eu.codetopic.utils.module.getter;

import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.ModulesManager;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public class ModuleGetterImpl<MT extends Module> implements ModuleGetter<MT> {

    private final Class<MT> mModuleClass;

    public ModuleGetterImpl(Class<MT> moduleClass) {
        mModuleClass = moduleClass;
    }

    public Class<MT> getModuleClass() {
        return mModuleClass;
    }

    public MT getModule() {
        return ModulesManager.findModule(getModuleClass());
    }
}

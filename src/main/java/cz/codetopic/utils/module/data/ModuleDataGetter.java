package cz.codetopic.utils.module.data;

import cz.codetopic.utils.module.Module;
import cz.codetopic.utils.module.ModuleGetter;

/**
 * Created by anty on 24.2.16.
 *
 * @author anty
 */
public class ModuleDataGetter<MT extends Module, DT extends ModuleData> extends ModuleGetter<MT> {

    private final Class<DT> mModuleDataClass;

    public ModuleDataGetter(Class<MT> moduleClass, Class<DT> moduleDataClass) {
        super(moduleClass);
        mModuleDataClass = moduleDataClass;
    }

    public boolean validate() {
        return get() != null;
    }

    public DT get() {
        MT module = getModule();
        return module == null ? null : module.findModuleData(getDataClass());
    }

    public Class<DT> getDataClass() {
        return mModuleDataClass;
    }
}

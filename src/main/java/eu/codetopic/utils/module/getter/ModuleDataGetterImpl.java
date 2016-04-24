package eu.codetopic.utils.module.getter;

import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.data.ModuleData;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public class ModuleDataGetterImpl<MT extends Module, DT extends ModuleData>
        extends ModuleGetterImpl<MT> implements ModuleDataGetter<MT, DT> {

    private final Class<DT> mModuleDataClass;

    public ModuleDataGetterImpl(Class<MT> moduleClass, Class<DT> moduleDataClass) {
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

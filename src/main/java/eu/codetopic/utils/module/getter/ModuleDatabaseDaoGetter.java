package eu.codetopic.utils.module.getter;

import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.data.ModuleDatabase;

/**
 * Created by anty on 25.2.16.
 *
 * @author anty
 */
public interface ModuleDatabaseDaoGetter<MT extends Module, DT> extends ModuleGetter<MT>, DatabaseDaoGetter<DT> {

    ModuleDatabase getDatabase();
}

package eu.codetopic.utils.module.getter;

import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.data.ModuleData;

/**
 * Created by anty on 24.2.16.
 *
 * @author anty
 */
public interface ModuleDataGetter<MT extends Module, DT extends ModuleData>
        extends ModuleGetter<MT>, DataGetter<DT> {
}

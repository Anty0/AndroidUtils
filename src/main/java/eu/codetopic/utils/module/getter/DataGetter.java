package eu.codetopic.utils.module.getter;

import java.io.Serializable;

import eu.codetopic.utils.module.data.ModuleData;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public interface DataGetter<DT extends ModuleData> extends Serializable {

    DT get();

    Class<DT> getDataClass();
}

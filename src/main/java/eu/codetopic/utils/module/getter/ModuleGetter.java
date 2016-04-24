package eu.codetopic.utils.module.getter;

import java.io.Serializable;

import eu.codetopic.utils.module.Module;

/**
 * Created by anty on 18.3.16.
 *
 * @author anty
 */
public interface ModuleGetter<MT extends Module> extends Serializable {

    Class<MT> getModuleClass();

    MT getModule();
}

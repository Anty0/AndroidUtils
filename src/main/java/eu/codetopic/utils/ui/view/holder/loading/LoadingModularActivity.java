package eu.codetopic.utils.ui.view.holder.loading;

import org.apache.commons.lang3.ArrayUtils;

import eu.codetopic.utils.ui.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;

public abstract class LoadingModularActivity extends ModularActivity {

    private static final String LOG_TAG = "LoadingModularActivity";

    public LoadingModularActivity() {
        this(new ActivityCallBackModule[0]);
    }

    public LoadingModularActivity(ActivityCallBackModule... modules) {
        this(new LoadingModule(), modules);
    }

    public LoadingModularActivity(Class<? extends LoadingVH> holderClass,
                                  ActivityCallBackModule... modules) {
        this(new LoadingModule(holderClass), modules);
    }

    private LoadingModularActivity(LoadingModule loadingModule,
                                   ActivityCallBackModule... additionalModules) {
        super(ArrayUtils.add(additionalModules, 0, loadingModule));
    }

    public LoadingVH getHolder() {
        return findModule(LoadingModule.class).getHolder();
    }
}

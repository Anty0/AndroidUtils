package eu.codetopic.utils.view.holder.loading;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.activity.modular.ModularActivity;

public class LoadingModularActivity extends ModularActivity {

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
        super(Arrays.addToStart(additionalModules, loadingModule));
    }

    public LoadingVH getHolder() {
        return findModule(LoadingModule.class).getHolder();
    }
}

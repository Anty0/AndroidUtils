package com.codetopic.utils.view.holder.loading;

import com.codetopic.utils.Arrays;
import com.codetopic.utils.activity.modular.ActivityCallBackModule;
import com.codetopic.utils.activity.modular.ModularActivity;

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
        super(Arrays.addToStart(additionalModules, loadingModule));
    }

    public LoadingVH getHolder() {
        return findModule(LoadingModule.class).getHolder();
    }
}

package com.codetopic.utils.activity.loading;

import com.codetopic.utils.Arrays;
import com.codetopic.utils.activity.modular.ActivityCallBackModule;
import com.codetopic.utils.activity.modular.ModularActivity;

/**
 * Use LoadingModularActivity instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingModularActivity extends ModularActivity {

    private static final String LOG_TAG = "LoadingModularActivity";

    public LoadingModularActivity(ActivityCallBackModule... modules) {
        this(new LoadingModule(), modules);
    }

    public LoadingModularActivity(Class<? extends LoadingViewHolder> loadingViewHolderClass,
                                  ActivityCallBackModule... modules) {
        this(new LoadingModule(loadingViewHolderClass), modules);
    }

    private LoadingModularActivity(LoadingModule loadingModule,
                                   ActivityCallBackModule... additionalModules) {
        super(Arrays.addToStart(additionalModules, loadingModule));
    }

    public LoadingViewHolder getLoadingViewHolder() {
        return findModule(LoadingModule.class).getLoadingViewHolder();
    }

    public LoadingViewHolder.HolderInfo<?> getLoadingHolderInfo() {
        return findModule(LoadingModule.class).getLoadingHolderInfo();
    }
}

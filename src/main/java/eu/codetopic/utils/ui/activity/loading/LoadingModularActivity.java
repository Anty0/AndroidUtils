package eu.codetopic.utils.ui.activity.loading;

import org.apache.commons.lang3.ArrayUtils;

import eu.codetopic.utils.ui.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;

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
        super(ArrayUtils.add(additionalModules, 0, loadingModule));
    }

    public LoadingViewHolder getLoadingViewHolder() {
        return findModule(LoadingModule.class).getLoadingViewHolder();
    }

    public LoadingViewHolder.HolderInfo<?> getLoadingHolderInfo() {
        return findModule(LoadingModule.class).getLoadingHolderInfo();
    }
}

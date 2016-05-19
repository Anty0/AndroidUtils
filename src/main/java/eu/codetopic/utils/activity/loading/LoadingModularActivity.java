package eu.codetopic.utils.activity.loading;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.activity.modular.ModularActivity;

public class LoadingModularActivity extends ModularActivity {

    private static final String LOG_TAG = "LoadingModularActivity";

    public LoadingModularActivity(ActivityCallBackModule... modules) {
        super(Arrays.addToStart(modules, new LoadingModule()));
    }

    public LoadingViewHolder getLoadingViewHolder() {
        return findModule(LoadingModule.class).getLoadingViewHolder();
    }
}

package eu.codetopic.utils.ui.view.holder.loading;

import android.support.annotation.NonNull;

import eu.codetopic.utils.ui.view.holder.ViewHolderModule;

public class LoadingModule extends ViewHolderModule<LoadingVH> {

    private static final String LOG_TAG = "LoadingModule";

    public LoadingModule() {
        this(DefaultLoadingVH.class);
    }

    public LoadingModule(@NonNull Class<? extends LoadingVH> holderClass) {
        super(holderClass);
    }
}

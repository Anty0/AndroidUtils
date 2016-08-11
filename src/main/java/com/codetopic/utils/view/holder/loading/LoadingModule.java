package com.codetopic.utils.view.holder.loading;

import android.support.annotation.NonNull;

import com.codetopic.utils.view.holder.ViewHolderModule;

public class LoadingModule extends ViewHolderModule<LoadingVH> {

    private static final String LOG_TAG = "LoadingModule";

    public LoadingModule() {
        this(DefaultLoadingVH.class);
    }

    public LoadingModule(@NonNull Class<? extends LoadingVH> holderClass) {
        super(holderClass);
    }
}

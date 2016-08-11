package eu.codetopic.utils.view.holder.loading;

import android.support.annotation.NonNull;

import eu.codetopic.utils.view.holder.ViewHolderFragment;

public abstract class LoadingFragment extends ViewHolderFragment {

    private static final String LOG_TAG = "LoadingFragment";

    public LoadingFragment() {
        this(DefaultLoadingVH.class);
    }

    public LoadingFragment(@NonNull Class<? extends LoadingVH> holderClass) {
        super(holderClass);
    }

    public LoadingVH getHolder() {
        return (LoadingVH) super.getHolder(0);
    }
}

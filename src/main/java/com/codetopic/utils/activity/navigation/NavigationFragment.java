package com.codetopic.utils.activity.navigation;

import com.codetopic.utils.view.holder.loading.LoadingFragment;
import com.codetopic.utils.view.holder.loading.LoadingVH;

public abstract class NavigationFragment extends LoadingFragment {

    public NavigationFragment() {
    }

    public NavigationFragment(Class<? extends LoadingVH> loadingViewHolderClass) {
        super(loadingViewHolderClass);
    }

    protected NavigationActivity getNavigationActivity() {
        return (NavigationActivity) getActivity();
    }

    protected void switchFragment(Class<? extends NavigationFragment> clazz) {
        getNavigationActivity().replaceFragment(clazz);
    }

    protected void removeSelfFragment() {
        getNavigationActivity().removeCurrentFragment();
    }

}

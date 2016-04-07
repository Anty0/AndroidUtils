package cz.codetopic.utils.activity.navigation;

import cz.codetopic.utils.activity.loading.LoadingViewHolderFragment;

public abstract class NavigationFragment extends LoadingViewHolderFragment {

    protected NavigationActivity getNavigationActivity() {
        return (NavigationActivity) getActivity();
    }

    protected void switchFragment(Class<? extends NavigationFragment> clazz) {
        getNavigationActivity().replaceFragment(clazz);
    }

}

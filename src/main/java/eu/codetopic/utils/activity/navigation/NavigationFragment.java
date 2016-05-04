package eu.codetopic.utils.activity.navigation;

import eu.codetopic.utils.activity.loading.LoadingViewHolderFragment;

public abstract class NavigationFragment extends LoadingViewHolderFragment {

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

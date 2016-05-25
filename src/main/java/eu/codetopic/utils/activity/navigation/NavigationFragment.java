package eu.codetopic.utils.activity.navigation;

import eu.codetopic.utils.activity.loading.LoadingFragment;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;

public abstract class NavigationFragment extends LoadingFragment {

    public NavigationFragment() {
    }

    public NavigationFragment(Class<? extends LoadingViewHolder> loadingViewHolderClass) {
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

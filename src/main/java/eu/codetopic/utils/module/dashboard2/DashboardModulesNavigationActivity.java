package eu.codetopic.utils.module.dashboard2;

import android.support.v4.app.Fragment;

import eu.codetopic.utils.module.ModulesNavigationActivity;

/**
 * Created by anty on 2.5.16.
 *
 * @author anty
 */
public abstract class DashboardModulesNavigationActivity extends ModulesNavigationActivity {

    private static final String LOG_TAG = "DashboardModulesNavigationActivity";

    @Override
    protected Class<? extends Fragment> getMainFragmentClass() {
        return DashboardFragment.class;
    }
}

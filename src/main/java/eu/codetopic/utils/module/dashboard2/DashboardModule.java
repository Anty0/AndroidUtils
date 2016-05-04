package eu.codetopic.utils.module.dashboard2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.navigation.NavigationActivity;
import eu.codetopic.utils.module.ModuleImpl;
import eu.codetopic.utils.module.data.ModuleDataManager;

/**
 * Created by anty on 2.5.16.
 *
 * @author anty
 */
public final class DashboardModule extends ModuleImpl {

    private static final String LOG_TAG = "DashboardModule";
    private static int MODULE_PRIORITY = 100;

    public static void setDefaultModulePriority(int priority) {
        MODULE_PRIORITY = priority;
    }

    @NonNull
    @Override
    public CharSequence getName() {
        return "Dashboard";// TODO: 2.5.16 to strings
    }

    @Override
    public int getPriority() {
        return MODULE_PRIORITY;
    }

    @Nullable
    @Override
    protected ModuleDataManager onCreateDataManager() {
        return new ModuleDataManager(new DashboardData(this));
    }

    @Override
    public boolean onCreateNavigationMenu(final NavigationActivity activity, Menu menu) {
        menu.add(getName()).setIcon(R.drawable.ic_home_dashboard)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        activity.replaceFragment(DashboardFragment.class);
                        return true;
                    }
                });
        return true;
    }
}

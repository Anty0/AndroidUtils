package cz.codetopic.utils.module;

import android.support.annotation.Nullable;
import android.view.Menu;

import com.path.android.jobqueue.JobManager;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.activity.navigation.NavigationActivity;
import cz.codetopic.utils.module.component.ComponentsManager;
import cz.codetopic.utils.module.dashboard.DashboardItemsAdapter;
import cz.codetopic.utils.module.data.ModuleDataManager;
import cz.codetopic.utils.module.data.ModuleDatabase;
import cz.codetopic.utils.module.settings.Settings;

/**
 * Created by anty on 16.2.16.
 *
 * @author anty
 */
public abstract class ModuleImpl extends Module {

    private static final String LOG_TAG = "ModuleImpl";

    private ComponentsManager mComponentsManager;
    private ModuleDataManager mModuleDataManager;
    private JobManager mModuleJobManager;
    private ModuleDatabase mModuleDatabase;

    @Override
    protected void onCreate() {
        mComponentsManager = onCreateComponentsManager();
        mModuleJobManager = onCreateJobManager();
        mModuleDataManager = onCreateDataManager();
        mModuleDatabase = onCreateDatabase();
        if (mModuleDatabase != null && mModuleJobManager != null) {
            mModuleJobManager.stop();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mModuleDatabase.initDaos();
                    } catch (Throwable t) {
                        Log.e(LOG_TAG, "onCreate initDaos()", t);
                    } finally {
                        mModuleJobManager.start();
                    }
                }
            }).start();
        }
    }

    @Nullable
    protected ComponentsManager onCreateComponentsManager() {
        return null;
    }

    @Nullable
    @Override
    public ComponentsManager getComponentsManager() {
        return mComponentsManager;
    }

    @Nullable
    protected ModuleDataManager onCreateDataManager() {
        return null;
    }

    @Override
    @Nullable
    public ModuleDataManager getDataManager() {
        return mModuleDataManager;
    }

    protected boolean hasJobManager() {
        return false;
    }

    @Nullable
    protected JobManager onCreateJobManager() {
        return hasJobManager() ? new JobManager(this, getName() + "Jobs") : null;
    }

    @Override
    @Nullable
    public JobManager getJobManager() {
        return mModuleJobManager;
    }

    @Nullable
    protected ModuleDatabase onCreateDatabase() {
        return null;
    }

    @Override
    @Nullable
    public ModuleDatabase getDatabase() {
        return mModuleDatabase;
    }

    @Override
    public boolean onCreateNavigationMenu(NavigationActivity activity, Menu menu) {
        return false;
    }

    @Nullable
    @Override
    public DashboardItemsAdapter[] getDashboardItemsAdapters() {
        return new DashboardItemsAdapter[0];
    }

    @Override
    public boolean hasSettings() {
        return false;
    }

    @Nullable
    @Override
    public Settings getSettings() {
        return null;
    }
}

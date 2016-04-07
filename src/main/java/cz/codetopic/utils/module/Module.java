package cz.codetopic.utils.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.view.ContextThemeWrapper;
import android.view.Menu;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.JobManager;

import junit.framework.Assert;

import java.sql.SQLException;

import cz.codetopic.utils.OnceReceiver;
import cz.codetopic.utils.R;
import cz.codetopic.utils.Utils;
import cz.codetopic.utils.activity.navigation.NavigationActivity;
import cz.codetopic.utils.module.component.ComponentsManager;
import cz.codetopic.utils.module.dashboard.DashboardItemsAdapter;
import cz.codetopic.utils.module.data.ModuleData;
import cz.codetopic.utils.module.data.ModuleDataManager;
import cz.codetopic.utils.module.data.ModuleDatabase;
import cz.codetopic.utils.module.settings.Settings;

/**
 * Created by anty on 15.2.16.
 *
 * @author anty
 */
public abstract class Module extends ContextThemeWrapper {

    protected void validate() throws Throwable {
        Assert.assertNotNull(getName());
        if (hasSettings()) {
            Assert.assertNotNull(getSettingsName());
            Assert.assertNotNull(getSettings());
        }
        if (getDatabase() != null) Assert.assertNotNull(getJobManager());
    }

    final void attach(ModulesManager manager) {
        attachBaseContext(manager.getContext());
        ModuleTheme theme = getClass().getAnnotation(ModuleTheme.class);
        setTheme(theme == null ? manager.getDefaultTheme() : theme.value());
        onCreate();
    }

    protected abstract void onCreate();

    @NonNull
    public abstract CharSequence getName();

    @Nullable
    public abstract ComponentsManager getComponentsManager();// TODO: 13.3.16 implement component manager in all modules

    @Nullable
    public abstract ModuleDataManager getDataManager();

    public <D extends ModuleData> D findModuleData(Class<D> moduleDataClass) {
        ModuleDataManager moduleDataManager = getDataManager();
        return moduleDataManager == null ? null : moduleDataManager.find(moduleDataClass);
    }

    @Nullable
    public abstract ModuleDatabase getDatabase();

    @WorkerThread
    public <T> Dao<T, ?> getDatabaseDao(Class<T> dataObjectClass) throws SQLException {
        ModuleDatabase moduleDatabase = getDatabase();
        return moduleDatabase == null ? null : moduleDatabase.getDao(dataObjectClass);
    }

    @Nullable
    public abstract JobManager getJobManager();

    public abstract boolean onCreateNavigationMenu(NavigationActivity activity, Menu menu);

    public void replaceFragment(@Nullable Class fragmentClass) {
        replaceFragment(fragmentClass, null);
    }

    public void replaceFragment(@Nullable final Class fragmentClass, @Nullable final Bundle fragmentExtras) {
        startActivity(new Intent(this, ModulesNavigationActivity.class));
        new OnceReceiver(this, ModulesNavigationActivity.BROADCAST_ACTION_ACTIVITY_INITIALIZED) {
            @Override
            public void onReceived(Context context, Intent intent) {
                ModulesNavigationActivity.replaceFragment(context, fragmentClass, fragmentExtras);
            }
        };
    }

    public void invalidateNavigationMenu() {
        ModulesNavigationActivity.invalidateNavigationMenu(this);
    }

    @Nullable
    public abstract DashboardItemsAdapter[] getDashboardItemsAdapters();

    public abstract boolean hasSettings();

    @Nullable
    public CharSequence getSettingsName() {
        return Utils.getFormattedText(this, R.string.text_settings, getName());
    }

    @Nullable
    public abstract Settings getSettings();

    public void close() throws Throwable {
        ModuleDataManager dataManager = getDataManager();
        if (dataManager != null) dataManager.close();
        ModuleDatabase database = getDatabase();
        if (database != null) database.close();
    }
}

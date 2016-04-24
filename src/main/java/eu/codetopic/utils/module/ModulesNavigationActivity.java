package eu.codetopic.utils.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.navigation.NavigationActivity;
import eu.codetopic.utils.module.settings.SettingsActivity;

/**
 * Created by anty on 18.2.16.
 *
 * @author anty
 */
public abstract class ModulesNavigationActivity extends NavigationActivity {

    public static final String BROADCAST_ACTION_ACTIVITY_INITIALIZED =
            "eu.codetopic.utils.module.ModulesNavigationActivity.ACTIVITY_INITIALIZED";
    private static final String LOG_TAG = "ModulesNavigationActivity";
    private static final String BROADCAST_ACTION_REPLACE_FRAGMENT =
            "eu.codetopic.utils.module.ModulesNavigationActivity.REPLACE_FRAGMENT";
    private static final String EXTRA_FRAGMENT_CLASS =
            "eu.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";
    private static final String EXTRA_FRAGMENT_BUNDLE_EXTRAS =
            "eu.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";
    private static final String BROADCAST_ACTION_NAVIGATION_MENU_CHANGED =
            "eu.codetopic.utils.module.ModulesNavigationActivity.NAVIGATION_MENU_CHANGED";
    private final BroadcastReceiver mReplaceFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Class fragmentClass = (Class) intent.getSerializableExtra(EXTRA_FRAGMENT_CLASS);
            if (fragmentClass == null) removeCurrentFragment();
            else
                replaceFragment(fragmentClass, intent.getBundleExtra(EXTRA_FRAGMENT_BUNDLE_EXTRAS));
        }
    };
    private final BroadcastReceiver mNavigationMenuInvalidateReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    invalidateNavigationMenu();
                }
            };

    public static void replaceFragment(Context context, @Nullable Class fragmentClass, @Nullable Bundle extras) {
        context.sendBroadcast(new Intent(BROADCAST_ACTION_REPLACE_FRAGMENT)
                .putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass)
                .putExtra(EXTRA_FRAGMENT_BUNDLE_EXTRAS, extras));
    }

    public static void invalidateNavigationMenu(Context context) {
        context.sendBroadcast(new Intent(ModulesNavigationActivity
                .BROADCAST_ACTION_NAVIGATION_MENU_CHANGED));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mReplaceFragmentReceiver,
                new IntentFilter(BROADCAST_ACTION_REPLACE_FRAGMENT));
        registerReceiver(mNavigationMenuInvalidateReceiver,
                new IntentFilter(BROADCAST_ACTION_NAVIGATION_MENU_CHANGED));
        sendBroadcast(new Intent(BROADCAST_ACTION_ACTIVITY_INITIALIZED));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sendBroadcast(new Intent(BROADCAST_ACTION_ACTIVITY_INITIALIZED));
    }

    @Override
    protected boolean onCreateNavigationMenu(Menu menu) {
        super.onCreateNavigationMenu(menu);

        getMenuInflater().inflate(R.menu.menu_modules_navigation, menu);
        Menu modulesMenu = menu.findItem(R.id.modules_menu_item)
                .setTitle(getModulesSubMenuTitle()).getSubMenu();

        ArrayList<Module> modules = new ArrayList<>(ModulesManager.getInstance().getModules());
        Collections.sort(modules);
        for (Module module : modules) {
            try {
                Log.d(LOG_TAG, "onCreateNavigationMenu - " +
                        "Creating NavigationMenu for " + module.getName());
                module.onCreateNavigationMenu(this, modulesMenu);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onCreateNavigationMenu", e);
            }
        }

        menu.findItem(R.id.settings_menu_item).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(ModulesNavigationActivity.this, SettingsActivity.class));
                return true;
            }
        });
        return true;
    }

    protected CharSequence getModulesSubMenuTitle() {
        return "Modules";// TODO: 23.4.16 to strings
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNavigationMenuInvalidateReceiver);
        unregisterReceiver(mReplaceFragmentReceiver);
        super.onDestroy();
    }
}

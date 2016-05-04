package eu.codetopic.utils.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;

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

    public static final String EXTRA_FRAGMENT_CLASS =
            "eu.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";
    private static final String LOG_TAG = "ModulesNavigationActivity";
    /*public static final String BROADCAST_ACTION_ACTIVITY_INITIALIZED =
            "eu.codetopic.utils.module.ModulesNavigationActivity.ACTIVITY_INITIALIZED";*/
    /*private static final String BROADCAST_ACTION_REPLACE_FRAGMENT =
            "eu.codetopic.utils.module.ModulesNavigationActivity.REPLACE_FRAGMENT";
    private static final String EXTRA_FRAGMENT_CLASS =
            "eu.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";
    private static final String EXTRA_FRAGMENT_BUNDLE_EXTRAS =
            "eu.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";*/
    private static final String BROADCAST_ACTION_NAVIGATION_MENU_CHANGED =
            "eu.codetopic.utils.module.ModulesNavigationActivity.NAVIGATION_MENU_CHANGED";
    /*private final BroadcastReceiver mReplaceFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Class fragmentClass = (Class) intent.getSerializableExtra(EXTRA_FRAGMENT_CLASS);
            if (fragmentClass == null) removeCurrentFragment();
            else replaceFragment(fragmentClass, intent
                    .getBundleExtra(EXTRA_FRAGMENT_BUNDLE_EXTRAS));
        }
    };*/
    private final BroadcastReceiver mNavigationMenuInvalidateReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    invalidateNavigationMenu();
                }
            };

    public static void invalidateNavigationMenu(Context context) {
        context.sendBroadcast(new Intent(ModulesNavigationActivity
                .BROADCAST_ACTION_NAVIGATION_MENU_CHANGED));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*registerReceiver(mReplaceFragmentReceiver,
                new IntentFilter(BROADCAST_ACTION_REPLACE_FRAGMENT));*/
        registerReceiver(mNavigationMenuInvalidateReceiver,
                new IntentFilter(BROADCAST_ACTION_NAVIGATION_MENU_CHANGED));
        //sendBroadcast(new Intent(BROADCAST_ACTION_ACTIVITY_INITIALIZED));
        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //sendBroadcast(new Intent(BROADCAST_ACTION_ACTIVITY_INITIALIZED));
        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_FRAGMENT_CLASS))
            //noinspection unchecked
            replaceFragment((Class<? extends Fragment>) intent
                    .getSerializableExtra(EXTRA_FRAGMENT_CLASS));
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

        setupSettingsItem(menu);
        return true;
    }

    protected CharSequence getModulesSubMenuTitle() {
        return "Modules";// TODO: 23.4.16 to strings
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_modules_navigation, menu);
        menu.removeItem(R.id.modules_menu_item);
        setupSettingsItem(menu);
        return true;
    }

    private void setupSettingsItem(Menu menu) {
        menu.findItem(R.id.settings_menu_item).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(ModulesNavigationActivity.this, SettingsActivity.class));
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNavigationMenuInvalidateReceiver);
        //unregisterReceiver(mReplaceFragmentReceiver);
        super.onDestroy();
    }

    /*public static class FragmentReplaceReceiver extends BroadcastReceiver {

        private static final String LOG_TAG = "ModulesNavigationActivity$FragmentReplaceReceiver";
        private static final String EXTRA_ACTIVITY_CLASS =
                "eu.codetopic.utils.module.ModulesNavigationActivity$FragmentReplaceReceiver.ACTIVITY_CLASS";

        public static void replaceFragment(Context context, @Nullable Class fragmentClass,
                                           @Nullable Bundle extras) {
            Class<? extends ModulesNavigationActivity> activityClass = ModulesManager.getMainActivityClass();
            if (activityClass == null) {
                Log.e(LOG_TAG, "getReplaceFragmentIntent - no main activity class specified" +
                        " in ModulesManager, please specify it");
                return;
            }
            replaceFragment(context, activityClass, fragmentClass, extras);
        }

        public static void replaceFragment(Context context, @NonNull Class<? extends ModulesNavigationActivity> navigationActivityClass,
                                               @Nullable Class fragmentClass, @Nullable Bundle extras) {
            context.sendBroadcast(getReplaceFragmentIntent(context, navigationActivityClass, fragmentClass, extras));
        }

        public static Intent getReplaceFragmentIntent(Context context, @Nullable Class fragmentClass,
                                                      @Nullable Bundle extras) {
            Class<? extends ModulesNavigationActivity> activityClass = ModulesManager.getMainActivityClass();
            if (activityClass == null) {
                Log.e(LOG_TAG, "getReplaceFragmentIntent - no main activity class specified" +
                        " in ModulesManager, please specify it");
                return new Intent();
            }
            return getReplaceFragmentIntent(context, activityClass, fragmentClass, extras);
        }

        public static Intent getReplaceFragmentIntent(Context context, @NonNull Class<? extends ModulesNavigationActivity> navigationActivityClass,
                                               @Nullable Class fragmentClass, @Nullable Bundle extras) {
            return new Intent(context, FragmentReplaceReceiver.class)
                    .putExtra(EXTRA_ACTIVITY_CLASS, navigationActivityClass)
                    .putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass)
                    .putExtra(EXTRA_FRAGMENT_BUNDLE_EXTRAS, extras);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            new OnceReceiver(context.getApplicationContext(), ModulesNavigationActivity
                    .BROADCAST_ACTION_ACTIVITY_INITIALIZED) {
                @Override
                public void onReceived(Context context, Intent intent) {
                    context.sendBroadcast(new Intent(BROADCAST_ACTION_REPLACE_FRAGMENT)
                            .putExtras(intent.getExtras()));
                }
            };
            context.startActivity(new Intent(context, (Class<?>) intent
                    .getSerializableExtra(EXTRA_ACTIVITY_CLASS)));
        }
    }*/
}

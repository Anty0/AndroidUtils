package cz.codetopic.utils.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;
import cz.codetopic.utils.activity.navigation.NavigationActivity;
import cz.codetopic.utils.module.settings.SettingsActivity;

/**
 * Created by anty on 18.2.16.
 *
 * @author anty
 */
public abstract class ModulesNavigationActivity extends NavigationActivity {

    public static final String BROADCAST_ACTION_ACTIVITY_INITIALIZED =
            "cz.codetopic.utils.module.ModulesNavigationActivity.ACTIVITY_INITIALIZED";
    private static final String LOG_TAG = "ModulesNavigationActivity";
    private static final String BROADCAST_ACTION_REPLACE_FRAGMENT =
            "cz.codetopic.utils.module.ModulesNavigationActivity.REPLACE_FRAGMENT";
    private static final String EXTRA_FRAGMENT_CLASS =
            "cz.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";
    private static final String EXTRA_FRAGMENT_BUNDLE_EXTRAS =
            "cz.codetopic.utils.module.ModulesNavigationActivity.FRAGMENT_CLASS";
    private static final String BROADCAST_ACTION_NAVIGATION_MENU_CHANGED =
            "cz.codetopic.utils.module.ModulesNavigationActivity.NAVIGATION_MENU_CHANGED";
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
        boolean hasMenu = false;
        for (Module module : ModulesManager.getInstance().getModules()) {
            try {
                hasMenu |= module.onCreateNavigationMenu(this, menu);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onCreateNavigationMenu", e);
            }
        }
        menu.addSubMenu(R.string.menu_item_text_settings).getItem().setIcon(R.drawable.ic_action_settings)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        startActivity(new Intent(ModulesNavigationActivity.this, SettingsActivity.class));
                        return true;
                    }
                });
        return hasMenu;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mNavigationMenuInvalidateReceiver);
        unregisterReceiver(mReplaceFragmentReceiver);
        super.onDestroy();
    }
}

package eu.codetopic.utils.ui.activity.navigation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.activity.fragment.BaseFragmentActivity;

public abstract class NavigationActivity extends BaseFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "NavigationActivity";

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView textViewAppTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        textViewAppTitle = navigationView.getHeaderView(0)
                .findViewById(R.id.textViewAppTitle);

        setNavigationViewAppIconDrawable(AndroidUtils.getActivityIcon(this, getComponentName()));
        setHeaderBackgroundColor(AndroidUtils.getColorFromAttr(this,
                R.attr.colorPrimaryDark, Color.rgb(0, 0, 0)));

        textViewAppTitle.setText(getTitle());
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        invalidateNavigationMenu();
    }

    protected Class<? extends Fragment> getMainFragmentClass() {
        return null;
    }

    protected Fragment getMainFragment() {
        try {
            return getMainFragmentClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Fragment onCreateMainFragment() {
        try {
            return getMainFragment();
        } catch (Exception e) {
            Log.e(LOG_TAG, "onCreateMainFragment - provided wrong MainFragment", e);
            return null;
        }
    }

    public void setNavigationViewAppIconBitmap(Bitmap bm) {
        ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewAppIcon))
                .setImageBitmap(bm);
    }

    public void setNavigationViewAppIconDrawable(@Nullable Drawable drawable) {
        ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewAppIcon))
                .setImageDrawable(drawable);
    }

    public void setNavigationViewAppIconResource(@DrawableRes int resId) {
        ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewAppIcon))
                .setImageResource(resId);
    }

    public void setHeaderBackground(Drawable drawable) {
        //noinspection deprecation
        navigationView.getHeaderView(0).findViewById(R.id.navigationHeader)
                .setBackgroundDrawable(drawable);
    }

    public void setHeaderBackgroundColor(@ColorInt int color) {
        navigationView.getHeaderView(0).findViewById(R.id.navigationHeader)
                .setBackgroundColor(color);
    }

    public void setHeaderBackgroundResource(@DrawableRes int resId) {
        navigationView.getHeaderView(0).findViewById(R.id.navigationHeader)
                .setBackgroundResource(resId);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (textViewAppTitle != null) textViewAppTitle.setText(title);
    }

    @Override
    protected void onDestroy() {
        drawer = null;
        navigationView = null;
        textViewAppTitle = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment fragment = getCurrentFragment();
        Class<? extends Fragment> mainFragmentClass = getMainFragmentClass();
        if (fragment == null || !Objects.equals(fragment.getClass(), mainFragmentClass)) {
            replaceFragment(mainFragmentClass);
            return;
        }

        super.onBackPressed();
    }

    public void invalidateNavigationMenu() {
        if (navigationView == null) return;

        NavigationMenuPresenter presenter;
        try {
            Field presenterField = NavigationView.class.getDeclaredField("mPresenter");
            presenterField.setAccessible(true);
            presenter = (NavigationMenuPresenter) presenterField.get(navigationView);//this is HACK
        } catch (Exception e) {
            Log.w(LOG_TAG, "invalidateNavigationMenu - " +
                    "can't get menu presenter: can't get field from class", e);
            return;
        }

        if (presenter != null) presenter.setUpdateSuspended(true);

        Menu menu = navigationView.getMenu();
        menu.clear();
        onCreateNavigationMenu(menu);
        setupMenuItemsClickListeners(menu);
        resetNavigationView(getCurrentFragment());

        if (presenter != null) {
            presenter.setUpdateSuspended(false);
            presenter.updateMenuView(false);
        }
    }

    private void setupMenuItemsClickListeners(Menu menu) {
        Field listenerField;
        try {
            listenerField = MenuItemImpl.class.getDeclaredField("mClickListener");
            listenerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(LOG_TAG, "setupMenuItemsClickListeners - " +
                    "can't setup listeners: can't get field from class", e);
            return;
        }

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            try {
                if (item instanceof MenuItemImpl) {
                    final SupportMenuItem.OnMenuItemClickListener listener =
                            (SupportMenuItem.OnMenuItemClickListener) listenerField.get(item);//this is HACK
                    listenerField.set(item, new SupportMenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            drawer.closeDrawer(GravityCompat.START);
                            return listener != null && listener.onMenuItemClick(item);
                        }
                    });
                } else throw new ClassCastException("Wrong class: " + item.getClass());
            } catch (Throwable e) {
                Log.e(LOG_TAG, "setupMenuItemsClickListeners - " +
                        "can't setup listener for " + item, e);
            }
            if (item.hasSubMenu()) setupMenuItemsClickListeners(item.getSubMenu());
        }
    }

    private void resetNavigationView(Fragment currentFragment) {
        if (navigationView == null) return;
        onUpdateSelectedNavigationMenuItem(currentFragment, navigationView.getMenu());
    }

    /**
     * Override this method to enable navigation menu items selecting
     *
     * @param currentFragment current fragment
     * @param menu            navigation menu
     * @return true if checked state of any item was changed
     */
    protected boolean onUpdateSelectedNavigationMenuItem(@Nullable Fragment currentFragment, Menu menu) {
        if (currentFragment != null) {
            Log.e(LOG_TAG, "onUpdateSelectedNavigationMenuItem can't detect selected item for " + currentFragment.getClass() +
                    ", override method onUpdateSelectedNavigationMenuItem() and implement your own selected item detecting.");
            return false;
        }


        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        return true;
    }

    protected boolean onCreateNavigationMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (CURRENT_FRAGMENT_TAG.equals(fragment.getTag())) resetNavigationView(fragment);
    }

    @Override
    @SuppressWarnings("PrivateResource")
    protected void onBeforeReplaceFragment(FragmentTransaction ft, Fragment fragment) {
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}

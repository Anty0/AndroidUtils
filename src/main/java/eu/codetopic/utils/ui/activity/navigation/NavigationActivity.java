package eu.codetopic.utils.ui.activity.navigation;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.activity.fragment.BaseFragmentActivity;

public abstract class NavigationActivity extends BaseFragmentActivity {

    private static final String LOG_TAG = "NavigationActivity";

    private static final String KEY_SWITCHING_ACCOUNTS =
            "eu.codetopic.utils.ui.activity.navigation.NavigationActivity.SWITCHING_ACCOUNTS";

    private boolean mEnableSwitchingAccounts = false;
    private boolean mSwitchingAccounts = false;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private LinearLayout mHeaderView;

    private ImageView mAppIconView;

    private FrameLayout mContainerAppTitle;
    private TextView mTextViewAppTitle;

    private LinearLayout mContainerAccountsSwitch;
    private TextView mTextViewAccountName;
    private ImageButton mImageButtonEditAccount;
    private ImageView mImageViewAccountChangeArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSwitchingAccounts = savedInstanceState.getBoolean(KEY_SWITCHING_ACCOUNTS, false);
        }

        setContentView(R.layout.navigation_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0).findViewById(R.id.navigationHeader);

        mAppIconView = mHeaderView.findViewById(R.id.imageViewAppIcon);

        mContainerAppTitle = mHeaderView.findViewById(R.id.container_app_title);
        mTextViewAppTitle = mContainerAppTitle.findViewById(R.id.textViewAppTitle);

        mContainerAccountsSwitch = mHeaderView.findViewById(R.id.container_accounts_switch);
        mContainerAccountsSwitch.setOnClickListener(v -> toggleSwitchingAccounts());
        mTextViewAccountName = mContainerAccountsSwitch.findViewById(R.id.textViewAccountName);
        mImageButtonEditAccount = mContainerAccountsSwitch.findViewById(R.id.imageButtonEditAccount);
        mImageButtonEditAccount.setOnClickListener(this::onEditAccountButtonClick);
        mImageViewAccountChangeArrow = mContainerAccountsSwitch.findViewById(R.id.imageViewAccountChangeArrow);

        setNavigationViewAppIconDrawable(AndroidUtils.getActivityIcon(this, getComponentName()));
        setHeaderBackgroundColor(AndroidUtils.getColorFromAttr(this,
                R.attr.colorPrimaryDark, Color.rgb(0, 0, 0)));

        mTextViewAppTitle.setText(AndroidUtils.getAppLabel(this));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(item -> {
                    mDrawer.closeDrawer(GravityCompat.START);
                    if (mEnableSwitchingAccounts && mSwitchingAccounts) {
                        setSwitchingAccounts(false);
                        return onAccountNavigationItemSelected(item);
                    }
                    return onNavigationItemSelected(item);
                }
        );
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

    public void setActiveAccountEditButtonEnabled(boolean enabled) {
        mImageButtonEditAccount.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public boolean isActiveAccountEditButtonEnabled() {
        return mImageButtonEditAccount.getVisibility() == View.VISIBLE;
    }

    public void setEnableSwitchingAccounts(boolean enableSwitchingAccounts) {
        mEnableSwitchingAccounts = enableSwitchingAccounts;
        invalidateNavigationMenu();
    }

    public boolean isEnableSwitchingAccounts() {
        return mEnableSwitchingAccounts;
    }

    public boolean isSwitchingAccounts() {
        return mSwitchingAccounts;
    }

    public void setSwitchingAccounts(boolean switchingAccounts) {
        mSwitchingAccounts = switchingAccounts;
        invalidateNavigationMenu();
    }

    public boolean toggleSwitchingAccounts() {
        mSwitchingAccounts = !mSwitchingAccounts;
        invalidateNavigationMenu();
        return mSwitchingAccounts;
    }

    public void setNavigationViewAppIconBitmap(Bitmap bm) {
        mAppIconView.setImageBitmap(bm);
    }

    public void setNavigationViewAppIconDrawable(@Nullable Drawable drawable) {
        mAppIconView.setImageDrawable(drawable);
    }

    public void setNavigationViewAppIconResource(@DrawableRes int resId) {
        mAppIconView.setImageResource(resId);
    }

    public void setHeaderBackground(Drawable drawable) {
        ViewCompat.setBackground(mHeaderView, drawable);
    }

    public void setHeaderBackgroundColor(@ColorInt int color) {
        mHeaderView.setBackgroundColor(color);
    }

    public void setHeaderBackgroundResource(@DrawableRes int resId) {
        mHeaderView.setBackgroundResource(resId);
    }

    public void setNavigationTitle(CharSequence title) {
        mTextViewAppTitle.setText(title);
    }

    /*@Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mTextViewAppTitle != null) mTextViewAppTitle.setText(title);
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(KEY_SWITCHING_ACCOUNTS, mSwitchingAccounts);
    }

    @Override
    protected void onDestroy() {
        mDrawer = null;
        mNavigationView = null;
        mTextViewAppTitle = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
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

    @SuppressLint("RestrictedApi")
    public void invalidateNavigationMenu() {
        if (mNavigationView == null) return;

        if (mEnableSwitchingAccounts) {
            mTextViewAccountName.setText(onUpdateActiveAccountName());
            mImageViewAccountChangeArrow.setImageResource(mSwitchingAccounts
                    ? R.drawable.ic_arrow_drop_up
                    : R.drawable.ic_arrow_drop_down);
            mContainerAppTitle.setVisibility(View.GONE);
            mContainerAccountsSwitch.setVisibility(View.VISIBLE);
        } else {
            mContainerAccountsSwitch.setVisibility(View.GONE);
            mContainerAppTitle.setVisibility(View.VISIBLE);
        }

        NavigationMenuPresenter presenter = null;
        try {
            Field presenterField = NavigationView.class.getDeclaredField("mPresenter");
            presenterField.setAccessible(true);
            presenter = (NavigationMenuPresenter) presenterField.get(mNavigationView);//this is HACK
        } catch (Exception e) {
            Log.w(LOG_TAG, "invalidateNavigationMenu - " +
                    "can't get menu presenter: can't get field from class", e);
        }

        if (presenter != null) presenter.setUpdateSuspended(true);

        Menu menu = mNavigationView.getMenu();
        menu.clear();

        if (mEnableSwitchingAccounts && mSwitchingAccounts) {
            onCreateAccountNavigationMenu(menu);
            // setupMenuItemsClickListeners(menu);
        } else {
            onCreateNavigationMenu(menu);
            // setupMenuItemsClickListeners(menu);
        }
        resetNavigationView(getCurrentFragment());

        if (presenter != null) {
            presenter.setUpdateSuspended(false);
            presenter.updateMenuView(false);
        }
    }

    /*private void setupMenuItemsClickListeners(@NonNull Menu menu) {
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
                    listenerField.set(item, (SupportMenuItem.OnMenuItemClickListener) item1 -> {
                        boolean result = listener != null && listener.onMenuItemClick(item1);
                        mDrawer.closeDrawer(GravityCompat.START);
                        return result;
                    });
                } else throw new ClassCastException("Wrong class: " + item.getClass());
            } catch (Throwable e) {
                Log.e(LOG_TAG, "setupMenuItemsClickListeners - " +
                        "can't setup listener for " + item, e);
            }
            if (item.hasSubMenu()) setupMenuItemsClickListeners(item.getSubMenu());
        }
    }*/

    private void resetNavigationView(@Nullable Fragment currentFragment) {
        if (mNavigationView == null) return;
        if (mEnableSwitchingAccounts && mSwitchingAccounts) {
            onUpdateSelectedAccountNavigationMenuItem(currentFragment, mNavigationView.getMenu());
            mTextViewAccountName.setText(onUpdateActiveAccountName());
        } else {
            onUpdateSelectedNavigationMenuItem(currentFragment, mNavigationView.getMenu());
        }
    }

    protected boolean onCreateNavigationMenu(@NonNull Menu menu) {
        return false;
    }

    protected boolean onCreateAccountNavigationMenu(@NonNull Menu menu) {
        return false;
    }

    /**
     * Override this method to enable navigation menu items selecting
     *
     * @param currentFragment current fragment
     * @param menu            navigation menu
     * @return true if checked state of any item was changed
     */
    protected boolean onUpdateSelectedNavigationMenuItem(@Nullable Fragment currentFragment, @NonNull Menu menu) {
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

    protected boolean onUpdateSelectedAccountNavigationMenuItem(@Nullable Fragment currentFragment, @NonNull Menu menu) {
        Log.e(LOG_TAG, "onUpdateSelectedAccountNavigationMenuItem can't update selected account item" +
                ", override method onUpdateSelectedAccountNavigationMenuItem() and implement selected item detecting.");
        return false;
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public boolean onAccountNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    protected CharSequence onUpdateActiveAccountName() {
        Log.e(LOG_TAG, "onUpdateActiveAccountName can't get active account name" +
                ", override method onUpdateActiveAccountName() and implement active account name detecting.");
        return "";
    }

    protected boolean onEditAccountButtonClick(View v) {
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

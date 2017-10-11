package eu.codetopic.utils.ui.activity.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.R;

public abstract class BaseFragmentActivity extends AppCompatActivity {// TODO: 12.5.16 rework fragment replacing

    @IdRes public static final int CONTAINER_LAYOUT_ID = R.id.base_content;
    public static final String CURRENT_FRAGMENT_TAG =
            "BaseFragmentActivity.CURRENT_FRAGMENT";
    private static final String LOG_TAG = "BaseFragmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) replaceFragment(onCreateMainFragment());
    }

    @Nullable
    private <T extends Fragment> T initFragment(@NonNull Class<T> fragmentClass, @Nullable Bundle bundle) {
        try {
            T fragment = fragmentClass.newInstance();
            if (bundle != null) fragment.setArguments(bundle);
            return fragment;
        } catch (Exception e) {
            Log.e(LOG_TAG, "initFragment", e);
        }
        return null;
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT_TAG);
    }

    public void removeCurrentFragment() {
        replaceFragment((Fragment) null);
    }

    public <T extends Fragment> T replaceFragment(@NonNull Class<T> fragmentClass) {
        return replaceFragment(fragmentClass, null);
    }

    public <T extends Fragment> T replaceFragment(@NonNull Class<T> fragmentClass, @Nullable Bundle bundle) {
        return replaceFragment(initFragment(fragmentClass, bundle));
    }

    public <T extends Fragment> T replaceFragment(@NonNull FragmentTransaction ft,
                                                  @NonNull Class<T> fragmentClass, @Nullable Bundle bundle) {
        return replaceFragment(ft, initFragment(fragmentClass, bundle));
    }

    public <T extends Fragment> T replaceFragment(@Nullable T fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        try {
            return replaceFragment(ft, fragment);
        } finally {
            ft.commitAllowingStateLoss();
        }
    }

    public <T extends Fragment> T replaceFragment(@NonNull FragmentTransaction ft, @Nullable T fragment) {
        onBeforeReplaceFragment(ft, fragment);
        Fragment currentFragment = getCurrentFragment();

        if (fragment != null) {
            if (currentFragment == null
                    || !Objects.equals(fragment.getClass(), currentFragment.getClass())
                    || !AndroidUtils.equalBundles(fragment.getArguments(), currentFragment.getArguments())) {
                ft.replace(CONTAINER_LAYOUT_ID, fragment, CURRENT_FRAGMENT_TAG);
            }
            return fragment;
        }

        if (currentFragment != null) ft.remove(currentFragment);
        setTitle(AndroidUtils.getApplicationLabel(this));
        return null;
    }

    protected void onBeforeReplaceFragment(FragmentTransaction ft, Fragment fragment) {
    }

    protected abstract Fragment onCreateMainFragment();

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (CURRENT_FRAGMENT_TAG.equals(fragment.getTag())) {
            if (fragment instanceof TitleProvider) {
                setTitle(((TitleProvider) fragment).getTitle());
            }
            if (fragment instanceof ThemeProvider) {
                int themeId = ((ThemeProvider) fragment).getThemeId();
                Context themedContext = new ContextThemeWrapper(getLayoutInflater().getContext(), themeId);
                Toolbar toolbar = findViewById(R.id.toolbar);
                if (toolbar != null) {

                    int backgroundColor = AndroidUtils.getColorFromAttr(
                            themedContext, R.attr.colorPrimary, -1);
                    if (backgroundColor != -1) toolbar.setBackgroundColor(backgroundColor);

                    int titleTextColor = AndroidUtils.getColorFromAttr(
                            themedContext, R.attr.titleTextColor, -1);
                    if (titleTextColor != -1) toolbar.setTitleTextColor(titleTextColor);

                    int subtitleTextColor = AndroidUtils.getColorFromAttr(
                            themedContext, R.attr.subtitleTextColor, -1);
                    if (subtitleTextColor != -1) toolbar.setSubtitleTextColor(subtitleTextColor);

                    if (Build.VERSION.SDK_INT >= 21) {
                        int navigationBarColor = AndroidUtils.getColorFromAttr(
                                themedContext, android.R.attr.navigationBarColor, -1);
                        if (navigationBarColor != -1)
                            getWindow().setNavigationBarColor(navigationBarColor);

                        int statusBarColor = AndroidUtils.getColorFromAttr(
                                themedContext, android.R.attr.statusBarColor, -1);
                        if (statusBarColor != -1)
                            getWindow().setStatusBarColor(statusBarColor);
                    }
                }

                NavigationView navView = findViewById(R.id.nav_view);
                if (navView != null) {
                    View headerView = navView.getHeaderView(0).findViewById(R.id.navigationHeader);
                    if (headerView != null) {
                        int headerBackgroundColor = AndroidUtils.getColorFromAttr(
                                themedContext, R.attr.colorPrimaryDark, -1);
                        if (headerBackgroundColor != -1)
                            headerView.setBackgroundColor(headerBackgroundColor);
                    }
                }
            }
        }

        //System.runFinalization();
        //System.gc();
    }
}
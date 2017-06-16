package eu.codetopic.utils.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
            /*if (fragment instanceof ToolbarThemeProvider) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                if (toolbar != null) {
                    toolbar.setBackgroundColor();
                    toolbar.setTitleTextColor();
                }
            }*/
        }

        //System.runFinalization();
        //System.gc();
    }
}
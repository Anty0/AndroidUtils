package eu.codetopic.utils.activity.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.Objects;
import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;

public abstract class BaseFragmentActivity extends AppCompatActivity {// TODO: 12.5.16 rework fragment replacing

    @IdRes public static final int CONTAINER_LAYOUT_ID = R.id.base_content;
    public static final String CURRENT_FRAGMENT_TAG =
            "eu.codetopic.utils.activity.fragment.BaseFragmentActivity.CURRENT_FRAGMENT";
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
        } catch (Exception e) {
            Log.e(LOG_TAG, "replaceFragment", e);
        } finally {
            ft.commit();
        }
        return null;
    }

    public <T extends Fragment> T replaceFragment(@NonNull FragmentTransaction ft, @Nullable T fragment) {
        try {
            onBeforeReplaceFragment(ft, fragment);
            Fragment currentFragment = getCurrentFragment();

            if (fragment != null) {
                if (currentFragment == null
                        || !Objects.equals(fragment.getClass(), currentFragment.getClass())
                        || !Utils.equalBundles(fragment.getArguments(), currentFragment.getArguments()))
                    ft.replace(CONTAINER_LAYOUT_ID, fragment, CURRENT_FRAGMENT_TAG);
                return fragment;
            }

            if (currentFragment != null) ft.remove(currentFragment);
            setTitle(Utils.getApplicationName(this));

        } catch (Exception e) {
            Log.e(LOG_TAG, "replaceFragment", e);
        }
        return null;
    }

    protected void onBeforeReplaceFragment(FragmentTransaction ft, Fragment fragment) {
    }

    protected abstract Fragment onCreateMainFragment();

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof TitleProvider &&
                CURRENT_FRAGMENT_TAG.equals(fragment.getTag()))
            setTitle(((TitleProvider) fragment).getTitle());

        System.runFinalization();
        System.gc();
    }
}
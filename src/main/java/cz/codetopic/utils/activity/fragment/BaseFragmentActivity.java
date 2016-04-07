package cz.codetopic.utils.activity.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.Objects;
import cz.codetopic.utils.R;
import cz.codetopic.utils.Utils;

public abstract class BaseFragmentActivity extends AppCompatActivity {

    @IdRes
    public static final int CONTAINER_LAYOUT_ID = R.id.fragment_container;
    public static final String ROOT_FRAGMENT_TAG = "cz.anty.ROOT_FRAGMENT";
    private static final String LOG_TAG = "BaseFragmentActivity";

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }
        final String action = intent.getAction();
        if (action != null) {
            arguments.putString("_action", action);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            tryAttachFragment();
        }
    }

    private void tryAttachFragment() {
        final Fragment fragment = onCreatePane();
        if (fragment == null)
            return;

        attachBundleToFragment(fragment);
        getSupportFragmentManager().beginTransaction()
                .add(CONTAINER_LAYOUT_ID, fragment, ROOT_FRAGMENT_TAG).commit();
    }

    private void attachBundleToFragment(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putAll(intentToFragmentArguments(getIntent()));
        fragment.setArguments(bundle);
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(ROOT_FRAGMENT_TAG);
    }

    public void removeCurrentFragment() {
        Fragment currentFrag = getSupportFragmentManager()
                .findFragmentByTag(ROOT_FRAGMENT_TAG);
        if (currentFrag != null)
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFrag).commit();
        updateTitle(null);
    }

    /**
     * Will replace existing fragment by new one.
     *
     * @param fragmentClass target fragment class
     */
    public <T extends Fragment> T replaceFragment(Class<T> fragmentClass) {
        return replaceFragment(fragmentClass, null);
    }

    /**
     * Will replace existing fragment by new one.
     *
     * @param fragmentClass target fragment class
     * @param bundle        bundle which will be available in new fragment
     */
    public <T extends Fragment> T replaceFragment(Class<T> fragmentClass, Bundle bundle) {
        Fragment currentFragment = getCurrentFragment();
        if (bundle == null && currentFragment != null && Objects.equals(currentFragment
                .getClass(), fragmentClass)) return null;
        try {
            T fragment = fragmentClass.newInstance();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            return replaceFragment(fragment);
        } catch (Exception e) {
            Log.e(LOG_TAG, "replaceFragment failed", e);
        }
        return null;
    }

    /**
     * Will replace existing fragment by new one.
     *
     * @param fragment target fragment
     */
    public <T extends Fragment> T replaceFragment(T fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        try {
            FragmentTransaction ft = fm.beginTransaction();
            onBeforeCommitReplaceFragment(fm, ft, fragment);
            if (fragment != null) {
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment == null || !Objects.equals(fragment
                        .getClass(), currentFragment.getClass()))
                    ft.replace(CONTAINER_LAYOUT_ID, fragment, ROOT_FRAGMENT_TAG);
            } else removeCurrentFragment();
            ft.commit();
            return fragment;
        } catch (Exception e) {
            Log.e(LOG_TAG, "replaceFragment failed", e);
        }
        return null;
    }

    /**
     * Will replace existing fragment by new one in desired transaction
     *
     * @param ft            active translation which weill be used for fragment replace
     * @param fragmentClass class of new fragment
     * @param bundle        bundle which will be available in new fragment
     */
    public <T extends Fragment> T replaceFragment(FragmentTransaction ft,
                                                  Class<T> fragmentClass, Bundle bundle) {
        Fragment currentFragment = getCurrentFragment();
        if (bundle == null && currentFragment != null && Objects.equals(currentFragment
                .getClass(), fragmentClass)) return null;
        try {
            T fragment = fragmentClass.newInstance();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            return replaceFragment(ft, fragment);
        } catch (Exception e) {
            Log.e(LOG_TAG, "replaceFragment failed", e);
        }
        return null;
    }

    /**
     * Will replace existing fragment by new one in desired transaction
     *
     * @param ft       active translation which weill be used for fragment replace
     * @param fragment target fragment instance
     */
    public <T extends Fragment> T replaceFragment(FragmentTransaction ft, T fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        try {
            onBeforeCommitReplaceFragment(fm, ft, fragment);
            if (fragment != null) {
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment == null || !Objects.equals(fragment
                        .getClass(), currentFragment.getClass()))
                    ft.replace(CONTAINER_LAYOUT_ID, fragment, ROOT_FRAGMENT_TAG);
            } else removeCurrentFragment();
            return fragment;
        } catch (Exception e) {
            Log.e(LOG_TAG, "replaceFragment failed", e);
        }
        return null;
    }

    /**
     * Called just before a fragment replacement transaction is committed in response to an intent
     * being fired and substituted for a fragment.
     * <p/>
     * Here you can put custom animation or any other customization for fragment replacement.
     */
    protected void onBeforeCommitReplaceFragment(FragmentManager fm, FragmentTransaction ft, Fragment fragment) {

    }

    /**
     * Called in <code>onCreate</code> when the fragment constituting this
     * activity is needed. The returned fragment's arguments will be set to the
     * intent used to invoke this activity.
     */
    protected abstract Fragment onCreatePane();

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        updateTitle(fragment);

        System.runFinalization();
        System.gc();
    }

    private void updateTitle(Fragment fragment) {
        setTitle(fragment instanceof TitleProvider ? ((TitleProvider) fragment)
                .getTitle() : Utils.getApplicationName(this));
    }
}
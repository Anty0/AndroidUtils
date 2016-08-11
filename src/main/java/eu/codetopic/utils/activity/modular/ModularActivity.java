package eu.codetopic.utils.activity.modular;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import eu.codetopic.utils.Log;

public abstract class ModularActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ModularActivity";

    private final LinkedHashMap<Class<? extends ActivityCallBackModule>,
            ActivityCallBackModule> mModules = new LinkedHashMap<>();

    public ModularActivity() {
        this(new ActivityCallBackModule[0]);
    }

    public ModularActivity(ActivityCallBackModule... modules) {
        if (Log.isInDebugMode())
            Log.d(LOG_TAG, "<init> using modules: " + Arrays.toString(modules));

        for (ActivityCallBackModule module : modules) {
            try {
                module.init(this);
                mModules.put(module.getClass(), module);
            } catch (Exception e) {
                Log.e(LOG_TAG, "<init>", e);
            }
        }
    }

    public Collection<ActivityCallBackModule> getModules() {
        return mModules.values();
    }

    public boolean hasModule(Class<? extends ActivityCallBackModule> moduleClass) {
        return mModules.containsKey(moduleClass);
    }

    public <T extends ActivityCallBackModule> T findModule(Class<T> moduleClass) {
        //noinspection unchecked
        return (T) mModules.get(moduleClass);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onCreate(savedInstanceState);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onCreate", e);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onNewIntent(intent);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onNewIntent", e);
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        try {
            SetContentViewCallBackImpl callBack = new SetContentViewCallBackImpl(layoutResID);
            for (ActivityCallBackModule module : getModules())
                callBack.callNext(module);
            callBack.apply();
        } catch (Exception e) {
            Log.e(LOG_TAG, "setContentView", e);
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        try {
            SetContentViewCallBackImpl callBack = new SetContentViewCallBackImpl(view);
            for (ActivityCallBackModule module : getModules())
                callBack.callNext(module);
            callBack.apply();
        } catch (Exception e) {
            Log.e(LOG_TAG, "setContentView", e);
            super.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        try {
            SetContentViewCallBackImpl callBack = new SetContentViewCallBackImpl(view, params);
            for (ActivityCallBackModule module : getModules())
                callBack.callNext(module);
            callBack.apply();
        } catch (Exception e) {
            Log.e(LOG_TAG, "setContentView", e);
            super.setContentView(view, params);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        Toolbar bak = toolbar;
        try {
            for (ActivityCallBackModule module : getModules())
                toolbar = module.onSetSupportActionBar(toolbar);
        } catch (Exception e) {
            Log.e(LOG_TAG, "setSupportActionBar", e);
            toolbar = bak;
        }
        super.setSupportActionBar(toolbar);
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onPostSetSupportActionBar(toolbar);
            } catch (Exception e) {
                Log.e(LOG_TAG, "setSupportActionBar", e);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onRestoreInstanceState(savedInstanceState);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onCreate", e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onStart();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onStart", e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onResume();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onResume", e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = false;
        for (ActivityCallBackModule module : getModules()) {
            try {
                result |= module.onCreateOptionsMenu(menu);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onCreateOptionsMenu", e);
            }
        }
        return super.onCreateOptionsMenu(menu) || result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        for (ActivityCallBackModule module : getModules()) {
            try {
                result = module.onOptionsItemSelected(item);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onKeyDown", e);
            }
            if (result) break;
        }
        return result || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onTitleChanged(title, color);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onTitleChanged", e);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onConfigurationChanged(newConfig);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onConfigurationChanged", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onBackPressed();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onBackPressed", e);
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = false;
        for (ActivityCallBackModule module : getModules()) {
            try {
                result = module.onKeyDown(keyCode, event);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onKeyDown", e);
            }
            if (result) break;
        }
        return result || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onAttachFragment(fragment);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onAttachFragment", e);
            }
        }
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onSaveInstanceState(outState);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onSaveInstanceState", e);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onPause();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onPause", e);
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onStop();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onStop", e);
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onDestroy();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onDestroy", e);
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for (ActivityCallBackModule module : getModules()) {
            try {
                module.onRestart();
            } catch (Exception e) {
                Log.e(LOG_TAG, "onRestart", e);
            }
        }
    }

    private class SetContentViewCallBackImpl implements ActivityCallBackModule.SetContentViewCallBack {

        private final List<Runnable> completedCallBacks = new ArrayList<>();
        private final List<Runnable> completedCallBacksTmp = new ArrayList<>();
        private boolean used = false;

        private Integer layoutResID = null;
        private View view = null;
        private ViewGroup.LayoutParams params = null;

        private SetContentViewCallBackImpl(@LayoutRes int layoutResID) {
            set(layoutResID);
        }

        private SetContentViewCallBackImpl(View view) {
            set(view);
        }

        private SetContentViewCallBackImpl(View view, ViewGroup.LayoutParams params) {
            set(view, params);
        }

        public void callNext(ActivityCallBackModule module) {
            used = false;
            completedCallBacks.addAll(0, completedCallBacksTmp);
            completedCallBacksTmp.clear();
            if (layoutResID != null) {
                module.onSetContentView(layoutResID, this);
            } else if (view != null) {
                if (params != null) {
                    module.onSetContentView(view, params, this);
                } else {
                    module.onSetContentView(view, this);
                }
            } else {
                throw new IllegalStateException("Nothing to call");
            }
            if (!used) throw new IllegalStateException("Module didn't call any method.");
        }

        public void apply() {
            completedCallBacks.addAll(0, completedCallBacksTmp);
            completedCallBacksTmp.clear();
            if (layoutResID != null) {
                ModularActivity.super.setContentView(layoutResID);
            } else if (view != null) {
                if (params != null) {
                    ModularActivity.super.setContentView(view, params);
                } else {
                    ModularActivity.super.setContentView(view);
                }
            } else {
                throw new IllegalStateException("Nothing to call");
            }
            for (Runnable callBack : completedCallBacks)
                callBack.run();
            completedCallBacks.clear();
            resetValues();
        }

        private void resetValues() {
            layoutResID = null;
            view = null;
            params = null;
        }

        private void reset() {
            reset(true);
        }

        private void reset(boolean resetValues) {
            if (used)
                throw new IllegalStateException("You can't call set() function more then one time");
            if (resetValues) resetValues();
            used = true;
        }

        @Override
        public void set(@LayoutRes int layoutResID) {
            reset();
            this.layoutResID = layoutResID;
        }

        @Override
        public void set(View view) {
            if (view == null) throw new NullPointerException("Can't set view to null");
            reset();
            this.view = view;
        }

        @Override
        public void set(View view, ViewGroup.LayoutParams params) {
            if (view == null) throw new NullPointerException("Can't set view to null");
            reset();
            this.view = view;
            this.params = params;
        }

        @Override
        public void pass() {
            reset(false);
        }

        @Override
        public void addViewAttachedCallBack(Runnable callBack) {
            completedCallBacksTmp.add(callBack);
        }

    }
}

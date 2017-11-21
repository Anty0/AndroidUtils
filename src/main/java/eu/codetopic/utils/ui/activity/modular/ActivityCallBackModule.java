/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.activity.modular;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class ActivityCallBackModule {

    private static final String LOG_TAG = "ActivityCallBackModule";

    private ModularActivity mActivity = null;

    final void init(@NonNull ModularActivity activity) {
        if (mActivity != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mActivity = activity;
    }

    public ModularActivity getActivity() {
        return mActivity;
    }

    protected abstract void onCreate(@Nullable Bundle savedInstanceState);

    protected abstract void onNewIntent(Intent intent);

    protected abstract void onSetContentView(@LayoutRes int layoutResID,
                                             SetContentViewCallBack callBack);

    protected abstract void onSetContentView(View view, SetContentViewCallBack callBack);

    protected abstract void onSetContentView(View view, ViewGroup.LayoutParams params,
                                             SetContentViewCallBack callBack);

    @Nullable
    protected abstract Toolbar onSetSupportActionBar(@Nullable Toolbar toolbar);

    protected abstract void onPostSetSupportActionBar(@Nullable Toolbar toolbar);

    protected abstract void onRestoreInstanceState(Bundle savedInstanceState);

    protected abstract void onStart();

    protected abstract void onResume();

    protected abstract boolean onCreateOptionsMenu(Menu menu);

    protected abstract boolean onOptionsItemSelected(MenuItem item);

    protected abstract void onTitleChanged(CharSequence title, int color);

    protected abstract void onConfigurationChanged(Configuration newConfig);

    protected abstract void onBackPressed();

    protected abstract boolean onKeyDown(int keyCode, KeyEvent event);

    protected abstract void onAttachFragment(Fragment fragment);

    protected abstract void onSaveInstanceState(Bundle outState);

    protected abstract void onPause();

    protected abstract void onStop();

    protected abstract void onDestroy();

    protected abstract void onRestart();

    public interface SetContentViewCallBack {

        void set(@LayoutRes int layoutResID);

        void set(View view);

        void set(View view, ViewGroup.LayoutParams params);

        void pass();

        void addViewAttachedCallBack(Runnable callBack);

    }
}

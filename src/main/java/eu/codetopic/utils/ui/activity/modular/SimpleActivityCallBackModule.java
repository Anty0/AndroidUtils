/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.ui.activity.modular;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SimpleActivityCallBackModule extends ActivityCallBackModule {

    private static final String LOG_TAG = "SimpleActivityCallBackModule";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
    }

    @Override
    protected void onSetContentView(@LayoutRes int layoutResID, SetContentViewCallBack callBack) {
        callBack.pass();
    }

    @Override
    protected void onSetContentView(View view, SetContentViewCallBack callBack) {
        callBack.pass();
    }

    @Override
    protected void onSetContentView(View view, ViewGroup.LayoutParams params, SetContentViewCallBack callBack) {
        callBack.pass();
    }

    @Override
    protected Toolbar onSetSupportActionBar(@Nullable Toolbar toolbar) {
        return toolbar;
    }

    @Override
    protected void onPostSetSupportActionBar(@Nullable Toolbar toolbar) {
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onResume() {
    }

    @Override
    protected boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    protected void onBackPressed() {
    }

    @Override
    protected boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onAttachFragment(Fragment fragment) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onPause() {
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onRestart() {
    }
}

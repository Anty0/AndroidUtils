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

package eu.codetopic.utils.ui.activity.modular.module;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

public class ToolbarModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "ToolbarModule";

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(() -> {
            ModularActivity activity = getActivity();
            activity.getLayoutInflater().inflate(layoutResID, activity.findViewById(R.id.base_content));
        });
        setupCallback(callBack);
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_content)).addView(view);
        });
        setupCallback(callBack);
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_content)).addView(view, params);
        });
        setupCallback(callBack);
    }

    private void setupCallback(SetContentViewCallBack callBack) {
        callBack.addViewAttachedCallBack(() -> {
            ModularActivity activity = getActivity();
            activity.setSupportActionBar(activity.findViewById(R.id.toolbar));
        });
    }
}

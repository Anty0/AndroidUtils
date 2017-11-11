/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

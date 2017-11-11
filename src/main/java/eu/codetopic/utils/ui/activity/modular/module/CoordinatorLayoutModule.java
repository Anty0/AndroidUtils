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
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

/**
 * Created by anty on 10/13/17.
 *
 * @author anty
 */
public class CoordinatorLayoutModule extends SimpleActivityCallBackModule {

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        callBack.set(R.layout.cordinator_layout_base);
        callBack.addViewAttachedCallBack(() -> {
            ModularActivity activity = getActivity();
            activity.getLayoutInflater().inflate(layoutResID, activity.findViewById(R.id.base_coordinator_layout_content));
        });
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        callBack.set(R.layout.cordinator_layout_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_coordinator_layout_content)).addView(view);
        });
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params, SetContentViewCallBack callBack) {
        callBack.set(R.layout.cordinator_layout_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_coordinator_layout_content)).addView(view, params);
        });
    }
}

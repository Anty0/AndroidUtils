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

package eu.codetopic.utils.ui.view.holder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

public class ViewHolderModule<VH extends ViewHolder> extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "ViewHolderModule";

    private final VH mHolder;

    public ViewHolderModule(@NonNull Class<? extends VH> holderClass) {
        VH holder;
        try {
            holder = holderClass.newInstance();
        } catch (Exception e) {
            holder = null;
            Log.e(LOG_TAG, "<init> can't create instance of " + holderClass.getName(), e);
        }
        mHolder = holder;
    }

    public VH getHolder() {
        return mHolder;
    }

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        final ViewHolder.ViewUpdater updater = mHolder.getViewUpdater();
        if (!updater.requiresBaseLayout()) {
            throw new IllegalStateException("Can't create contentView for ViewHolder without base layout");
            //callBack.pass();
            //return;
        }

        callBack.set(updater.getBaseLayoutResId());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                updater.applyOnBaseLayout(getActivity(),
                        new ViewHolder.LayoutViewCreator(layoutResID));
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        final ViewHolder.ViewUpdater updater = mHolder.getViewUpdater();
        if (!updater.requiresBaseLayout()) {
            throw new IllegalStateException("Can't create contentView for ViewHolder without base layout");
            //callBack.pass();
            //return;
        }

        callBack.set(updater.getBaseLayoutResId());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                updater.applyOnBaseLayout(getActivity(), new ViewHolder.ViewCreator() {
                    @Nullable
                    @Override
                    public View createView(Context context, ViewGroup parent) {
                        return view;
                    }
                });
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params,
                                    SetContentViewCallBack callBack) {
        throw new UnsupportedOperationException("Not supported");
    }

}

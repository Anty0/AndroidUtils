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

package eu.codetopic.utils.ui.activity.loading;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

/**
 * Use LoadingModule instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public class LoadingModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "LoadingModule";

    private final Class<? extends LoadingViewHolder> loadingViewHolderClass;
    private LoadingViewHolder loadingViewHolder = null;
    private LoadingViewHolder.HolderInfo<?> loadingHolderInfo = null;

    public LoadingModule() {
        this(DefaultLoadingViewHolder.class);
    }

    public LoadingModule(Class<? extends LoadingViewHolder> loadingViewHolderClass) {
        this.loadingViewHolderClass = loadingViewHolderClass;
    }

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        final LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap()) {
            callBack.pass();
            return;
        }

        callBack.set(holderInfo.getWrappingLayoutRes());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                ModularActivity activity = getActivity();
                activity.getLayoutInflater().inflate(layoutResID, (ViewGroup) activity
                        .findViewById(holderInfo.getContentLayoutId()));
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        final LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap()) {
            callBack.pass();
            return;
        }

        callBack.set(holderInfo.getWrappingLayoutRes());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(holderInfo
                        .getContentLayoutId())).addView(view);
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params,
                                    SetContentViewCallBack callBack) {

        final LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap()) {
            callBack.pass();
            return;
        }

        callBack.set(holderInfo.getWrappingLayoutRes());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(holderInfo
                        .getContentLayoutId())).addView(view, params);
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (loadingViewHolder != null)
            loadingViewHolder.clearViews();
        super.onDestroy();
    }

    protected void updateViewHolder() {
        if (loadingViewHolder == null) return;
        ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
        loadingViewHolder.updateViews(root != null && root
                .getChildCount() > 0 ? root.getChildAt(0) : null);
    }

    public LoadingViewHolder getLoadingViewHolder() {
        if (loadingViewHolder == null) {
            loadingViewHolder = LoadingViewHolder
                    .getInstance(getLoadingHolderInfo());
            updateViewHolder();
        }
        return loadingViewHolder;
    }

    public LoadingViewHolder.HolderInfo<?> getLoadingHolderInfo() {
        if (loadingHolderInfo == null)
            loadingHolderInfo = LoadingViewHolder
                    .getLoadingHolderInfo(loadingViewHolderClass);
        return loadingHolderInfo;
    }

}

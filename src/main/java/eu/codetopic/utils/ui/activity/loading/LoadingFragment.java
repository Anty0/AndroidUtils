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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.java.utils.log.Log;

/**
 * Use LoadingFragment instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingFragment extends Fragment {

    private static final String LOG_TAG = "LoadingFragment";

    private final Class<? extends LoadingViewHolder> loadingViewHolderClass;
    private LoadingViewHolder loadingViewHolder = null;
    private LoadingViewHolder.HolderInfo<?> loadingHolderInfo = null;

    public LoadingFragment() {
        this(DefaultLoadingViewHolder.class);
    }

    public LoadingFragment(Class<? extends LoadingViewHolder> loadingViewHolderClass) {
        this.loadingViewHolderClass = loadingViewHolderClass;
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap())
            return onCreateContentView(inflater, container, savedInstanceState);

        View base = inflater.inflate(holderInfo.getWrappingLayoutRes(), container, false);
        ViewGroup contentView = (ViewGroup) base.findViewById(holderInfo.getContentLayoutId());
        View view = onCreateContentView(inflater, contentView, savedInstanceState);
        if (view == null) return null;
        contentView.addView(view);
        return base;
    }

    @Nullable
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateViewHolder(view);
    }

    private void updateViewHolder(View view) {
        try {
            if (loadingViewHolder != null) loadingViewHolder.updateViews(view);
        } catch (Exception e) {
            Log.e(LOG_TAG, "onViewCreated", e);
        }
    }

    @Override
    public void onDestroyView() {
        if (loadingViewHolder != null) loadingViewHolder.clearViews();
        super.onDestroyView();
    }

    public LoadingViewHolder getLoadingViewHolder() {
        if (loadingViewHolder == null) {
            loadingViewHolder = LoadingViewHolder
                    .getInstance(getLoadingHolderInfo());
            updateViewHolder(getView());
        }
        return loadingViewHolder;
    }

    public LoadingViewHolder.HolderInfo<?> getLoadingHolderInfo() {
        if (loadingHolderInfo == null) {
            loadingHolderInfo = LoadingViewHolder
                    .getLoadingHolderInfo(loadingViewHolderClass);
        }
        return loadingHolderInfo;
    }

}

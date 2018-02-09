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

package eu.codetopic.utils.ui.container.recycler;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.container.swipe.SwipeLayoutInflater;

public abstract class RecyclerInflater<T extends RecyclerInflater<T, E>, E extends RecyclerManager<E>> extends SwipeLayoutInflater<T, E> {

    @LayoutRes public static final int DEFAULT_RECYCLER_LAYOUT_ID = R.layout.base_recycler;
    @IdRes public static final int DEFAULT_CONTENT_VIEW_ID = R.id.container_content;
    private static final String LOG_TAG = "RecyclerInflater";
    @LayoutRes private int mRecyclerLayoutResId = DEFAULT_RECYCLER_LAYOUT_ID;
    @IdRes private int mContentId = DEFAULT_CONTENT_VIEW_ID;
    private RecyclerView.LayoutManager mLayoutManager = null;
    private boolean mUseItemDivider = false;

    protected RecyclerInflater() {
    }

    public T withRecyclerLayoutResId(@LayoutRes int recyclerLayoutResId) {
        setRecyclerLayoutResId(recyclerLayoutResId);
        return self();
    }

    protected int getRecyclerLayoutResId() {
        return mRecyclerLayoutResId;
    }

    protected void setRecyclerLayoutResId(@LayoutRes int recyclerLayoutResId) {
        this.mRecyclerLayoutResId = recyclerLayoutResId;
    }

    public T withRecyclerContentId(@IdRes int recyclerContentId) {
        setRecyclerContentId(recyclerContentId);
        return self();
    }

    protected int getRecyclerContentId() {
        return mContentId;
    }

    protected void setRecyclerContentId(@IdRes int recyclerContentId) {
        this.mContentId = recyclerContentId;
    }

    public T withLayoutManager(RecyclerView.LayoutManager layoutManager) {
        setLayoutManager(layoutManager);
        return self();
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public T withItemDivider() {
        setUseItemDivider(true);
        return self();
    }

    public T withoutItemDivider() {
        setUseItemDivider(false);
        return self();
    }

    public boolean isUseItemDivider() {
        return mUseItemDivider;
    }

    public void setUseItemDivider(boolean useItemDivider) {
        this.mUseItemDivider = useItemDivider;
    }

    protected View addRecyclerViewTo(@NonNull LayoutInflater inflater, View view) {
        View contentView = view.findViewById(getRecyclerContentId());
        if (contentView instanceof ViewGroup)
            inflater.inflate(getRecyclerLayoutResId(), ((ViewGroup) contentView), true);
        return view;
    }

    @Override
    protected View initViewFor(@NonNull Activity activity) {
        return addRecyclerViewTo(activity.getLayoutInflater(), super.initViewFor(activity));
    }

    @Override
    protected View initViewFor(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToRoot) {
        return addRecyclerViewTo(inflater, super.initViewFor(inflater, parent, attachToRoot));
    }
}

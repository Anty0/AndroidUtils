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

package eu.codetopic.utils.ui.container.list;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.container.swipe.SwipeLayoutInflater;

public abstract class ListViewInflater<T extends ListViewInflater<T, E>, E extends ListViewManager<E>> extends SwipeLayoutInflater<T, E> {

    @LayoutRes public static final int DEFAULT_LIST_VIEW_LAYOUT_ID = R.layout.listview_base;
    @IdRes public static final int DEFAULT_CONTENT_VIEW_ID = R.id.container_content;
    private static final String LOG_TAG = "ListViewInflater";
    @LayoutRes private int mListViewLayoutResId = DEFAULT_LIST_VIEW_LAYOUT_ID;
    @IdRes private int mContentId = DEFAULT_CONTENT_VIEW_ID;

    protected ListViewInflater() {
    }

    public T withListViewLayoutResId(@LayoutRes int listViewLayoutResId) {
        setListViewLayoutResId(listViewLayoutResId);
        return self();
    }

    protected int getListViewLayoutResId() {
        return mListViewLayoutResId;
    }

    protected void setListViewLayoutResId(@LayoutRes int listViewLayoutResId) {
        this.mListViewLayoutResId = listViewLayoutResId;
    }

    public T withListViewContentId(@IdRes int listViewContentId) {
        setListViewContentId(listViewContentId);
        return self();
    }

    protected int getListViewContentId() {
        return mContentId;
    }

    protected void setListViewContentId(@IdRes int listViewContentId) {
        this.mContentId = listViewContentId;
    }

    protected View addListViewViewTo(@NonNull LayoutInflater inflater, View view) {
        View contentView = view.findViewById(getListViewContentId());
        if (contentView instanceof ViewGroup)
            inflater.inflate(getListViewLayoutResId(), ((ViewGroup) contentView), true);
        return view;
    }

    @Override
    protected View initViewFor(@NonNull Activity activity) {
        return addListViewViewTo(activity.getLayoutInflater(), super.initViewFor(activity));
    }

    @Override
    protected View initViewFor(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToRoot) {
        return addListViewViewTo(inflater, super.initViewFor(inflater, parent, attachToRoot));
    }
}

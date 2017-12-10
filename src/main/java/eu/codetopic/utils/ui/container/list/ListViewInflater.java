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

    @LayoutRes public static final int DEFAULT_LIST_VIEW_LAYOUT_ID = R.layout.base_listview;
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

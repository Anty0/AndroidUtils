package com.codetopic.utils.container.recycler;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codetopic.utils.R;
import com.codetopic.utils.container.swipe.SwipeLayoutInflater;

public abstract class RecyclerInflater<T extends RecyclerInflater<T, E>, E extends RecyclerManager<E>> extends SwipeLayoutInflater<T, E> {

    @LayoutRes public static final int DEFAULT_RECYCLER_LAYOUT_ID = R.layout.recycler_base;
    @IdRes public static final int DEFAULT_CONTENT_VIEW_ID = R.id.container_content;
    private static final String LOG_TAG = "RecyclerInflater";
    @LayoutRes private int mRecyclerLayoutResId = DEFAULT_RECYCLER_LAYOUT_ID;
    @IdRes private int mContentId = DEFAULT_CONTENT_VIEW_ID;

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

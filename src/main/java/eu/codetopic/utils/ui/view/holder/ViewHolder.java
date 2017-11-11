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

package eu.codetopic.utils.ui.view.holder;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ViewHolder {

    private static final String LOG_TAG = "ViewHolder";

    private final Object lock = new Object();
    private WeakReference<View> viewRef = new WeakReference<>(null);

    public ViewHolder() {

    }

    public final ViewUpdater getViewUpdater() {
        return new ViewUpdater();
    }

    @UiThread
    public final View updateView(Context context, ViewGroup parent, @LayoutRes int layoutId) {
        return updateView(context, parent, layoutId, true);
    }

    @UiThread
    public final View updateView(Context context, ViewGroup parent, @LayoutRes int layoutId,
                                 boolean attachToParent) {
        return updateView(context, parent, new LayoutViewCreator(layoutId), attachToParent);
    }

    @UiThread
    public final View updateView(Context context, ViewGroup parent, ViewCreator viewCreator) {
        return updateView(context, parent, viewCreator, true);
    }

    @UiThread
    public final View updateView(Context context, ViewGroup parent, ViewCreator viewCreator,
                                 boolean attachToParent) {
        View view;
        WrappingInfo wrappingInfo = getWrappingInfo();
        if (wrappingInfo != null) {
            view = LayoutInflater.from(context)
                    .inflate(wrappingInfo.getWrappingLayoutRes(), parent, attachToParent);

            parent = (ViewGroup) view.findViewById(wrappingInfo.getContentViewId());
            View result = viewCreator.createView(context, parent);
            if (result != null) parent.addView(result);
        } else {
            view = viewCreator.createView(context, parent);
            if (view != null && attachToParent) parent.addView(view);
        }

        setView(view);
        return view;
    }

    @UiThread
    protected void onUpdateView(@Nullable View view) {

    }

    @UiThread
    protected void onViewUpdated() {

    }

    @NonNull
    public Reference<View> getViewRef() {
        synchronized (lock) {
            return viewRef;
        }
    }

    @UiThread
    @Nullable
    public View getView() {
        return getViewRef().get();
    }

    @UiThread
    private void setView(@Nullable View view) {
        synchronized (lock) {
            viewRef = new WeakReference<>(view);
            onUpdateView(view);
            onViewUpdated();
        }
    }

    @NonNull
    public Object getViewLock() {
        return lock;
    }

    @Nullable
    protected WrappingInfo getWrappingInfo() {
        return null;
    }

    public interface ViewCreator {
        @Nullable
        @UiThread
        View createView(Context context, ViewGroup parent);
    }

    public static class LayoutViewCreator implements ViewCreator {

        private final int layoutId;

        public LayoutViewCreator(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
        }

        @UiThread
        @Nullable
        @Override
        public View createView(Context context, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(layoutId, parent, false);
        }
    }

    protected static class WrappingInfo implements Serializable {

        @LayoutRes private final int wrappingLayoutRes;
        @IdRes private final int contentViewId;

        public WrappingInfo(@LayoutRes int wrappingLayoutRes,
                            @IdRes int contentViewId) {

            this.wrappingLayoutRes = wrappingLayoutRes;
            this.contentViewId = contentViewId;
        }

        @LayoutRes
        @CheckResult
        public int getWrappingLayoutRes() {
            return wrappingLayoutRes;
        }

        @IdRes
        @CheckResult
        public int getContentViewId() {
            return contentViewId;
        }
    }

    @UiThread
    public final class ViewUpdater {

        private static final String LOG_TAG = ViewHolder.LOG_TAG + "$ViewUpdater";

        public boolean requiresBaseLayout() {
            return getWrappingInfo() != null;
        }

        @LayoutRes
        public int getBaseLayoutResId() {
            WrappingInfo wrappingInfo = getWrappingInfo();
            return wrappingInfo == null ? -1 : wrappingInfo.getWrappingLayoutRes();
        }

        public View applyOnParent(@NonNull Context context, @NonNull ViewGroup parent,
                                  @NonNull ViewCreator viewCreator) {

            if (requiresBaseLayout()) throw new IllegalArgumentException("Can't apply" +
                    " ViewHolder that requires base layout on parent.");

            View view = viewCreator.createView(context, parent);
            setView(view);
            return view;
        }

        public void applyOnBaseLayout(@NonNull Context context, @NonNull View baseView,
                                      @NonNull ViewCreator viewCreator) {

            WrappingInfo wrappingInfo = getWrappingInfo();
            if (wrappingInfo == null) throw new IllegalArgumentException("Can't apply ViewHolder" +
                    " that haven't got base layout on base layout");

            ViewGroup parent = (ViewGroup) baseView.findViewById(wrappingInfo.getContentViewId());
            View result = viewCreator.createView(context, parent);
            if (result != null) parent.addView(result);
            setView(baseView);
        }

        public void applyOnBaseLayout(@NonNull Activity activity, @NonNull ViewCreator viewCreator) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            applyOnBaseLayout(activity, decorView.getChildCount() == 1
                    ? decorView.getChildAt(0) : decorView, viewCreator);
        }
    }
}

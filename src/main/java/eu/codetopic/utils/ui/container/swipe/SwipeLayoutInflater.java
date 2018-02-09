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

package eu.codetopic.utils.ui.container.swipe;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;

public abstract class SwipeLayoutInflater<T extends SwipeLayoutInflater<T, E>, E extends SwipeLayoutManager> {

    @LayoutRes public static final int DEFAULT_SWIPE_LAYOUT_ID = R.layout.base_swipe_refresh;
    private static final String LOG_TAG = "SwipeLayoutInflater";
    private static int[] DEFAULT_SWIPE_SCHEME_COLORS = null;
    @LayoutRes private int mBaseLayoutResId = DEFAULT_SWIPE_LAYOUT_ID;
    private int[] mSwipeSchemeColors = DEFAULT_SWIPE_SCHEME_COLORS;
    private boolean mUseSwipeToRefresh = false;
    private boolean mUseFloatingActionButton = false;

    protected SwipeLayoutInflater() {
    }

    public static void setDefaultSwipeSchemeColors(int... defaultSwipeSchemeColors) {
        DEFAULT_SWIPE_SCHEME_COLORS = defaultSwipeSchemeColors;
    }

    protected abstract T self();

    public T withBaseLayoutResId(@LayoutRes int layoutResId) {
        setBaseLayoutResId(layoutResId);
        return self();
    }

    protected int getBaseLayoutResId() {
        return mBaseLayoutResId;
    }

    protected void setBaseLayoutResId(@LayoutRes int layoutResId) {
        this.mBaseLayoutResId = layoutResId;
    }

    public T withSwipeToRefresh() {
        setUseSwipeRefresh(true);
        return self();
    }

    public T withoutSwipeToRefresh() {
        setUseSwipeRefresh(false);
        return self();
    }

    protected boolean isUseSwipeRefresh() {
        return mUseSwipeToRefresh;
    }

    protected void setUseSwipeRefresh(boolean useSwipeToRefresh) {
        this.mUseSwipeToRefresh = useSwipeToRefresh;
    }

    public T withFloatingActionButton() {
        setUseFloatingActionButton(true);
        return self();
    }

    public T withoutFloatingActionButton() {
        setUseFloatingActionButton(false);
        return self();
    }

    protected boolean isUseFloatingActionButton() {
        return mUseFloatingActionButton;
    }

    protected void setUseFloatingActionButton(boolean useFloatingActionButton) {
        this.mUseFloatingActionButton = useFloatingActionButton;
    }

    public T withSchemeColors(int[] swipeToRefreshSchemeColors) {
        setSchemeColors(swipeToRefreshSchemeColors);
        return self();
    }

    protected int[] getSchemeColors() {
        return mSwipeSchemeColors;
    }

    protected void setSchemeColors(int[] swipeSchemeColors) {
        this.mSwipeSchemeColors = swipeSchemeColors;
    }

    protected abstract E getManagerInstance(View view);

    public E on(@NonNull Activity activity) {
        return getManagerInstance(initViewFor(activity));
    }

    public E on(@NonNull Context context, @Nullable ViewGroup parent, boolean attachToRoot) {
        return on(LayoutInflater.from(context), parent, attachToRoot);
    }

    public E on(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToRoot) {
        return getManagerInstance(initViewFor(inflater, parent, attachToRoot));
    }

    protected View initViewFor(@NonNull Activity activity) {
        activity.setContentView(getBaseLayoutResId());
        return activity.getWindow().getDecorView();
    }

    protected View initViewFor(@NonNull LayoutInflater inflater,
                               @Nullable ViewGroup parent, boolean attachToRoot) {

        return inflater.inflate(getBaseLayoutResId(), parent, attachToRoot);
    }

}

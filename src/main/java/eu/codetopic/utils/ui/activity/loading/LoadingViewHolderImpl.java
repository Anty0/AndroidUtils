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

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

import eu.codetopic.utils.ui.animation.ViewVisibilityAnimator;

/**
 * Use LoadingVHImpl instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingViewHolderImpl extends LoadingViewHolder {

    private static final String LOG_TAG = "LoadingViewHolderImpl";

    private View loading = null;
    private View content = null;

    @IdRes
    protected abstract int getContentViewId(Context context);

    @IdRes
    protected abstract int getLoadingViewId(Context context);

    public View getContentView() {
        return content;
    }

    public View getLoadingView() {
        return loading;
    }

    @Override
    protected void onUpdateMainView(@Nullable View newMainView) {
        if (newMainView != null) {
            content = newMainView.findViewById(getContentViewId(newMainView.getContext()));
            loading = newMainView.findViewById(getLoadingViewId(newMainView.getContext()));

            if (loading == null || content == null)
                throw new NullPointerException("Used view is not usable for " + LOG_TAG);
        } else {
            content = null;
            loading = null;
        }
    }

    protected void doShowLoading() {
        ViewVisibilityAnimator.getAnimatorFor(content).cancelAnimations();
        content.setVisibility(View.GONE);
        ViewVisibilityAnimator.getAnimatorFor(loading).animateVisibilityChange(true, loading
                .getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    protected void doHideLoading() {
        ViewVisibilityAnimator.getAnimatorFor(loading).animateVisibilityChange(false, loading
                .getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        ViewVisibilityAnimator.getAnimatorFor(content).animateVisibilityChange(true);
    }
}

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

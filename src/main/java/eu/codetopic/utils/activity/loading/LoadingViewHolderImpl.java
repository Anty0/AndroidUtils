package eu.codetopic.utils.activity.loading;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

import eu.codetopic.utils.animation.ViewVisibilityAnimator;

/**
 * Use eu.codetopic.utils.view.holder.loading.LoadingVHImpl instead
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

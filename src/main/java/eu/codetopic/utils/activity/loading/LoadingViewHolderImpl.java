package eu.codetopic.utils.activity.loading;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

import eu.codetopic.utils.simple.SimpleAnimatorListener;

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
        ViewCompat.animate(content).cancel();
        content.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT > 10) {
            Animator animator = AnimatorInflater.loadAnimator(loading.getContext(), android.R.animator.fade_in);
            animator.setDuration(loading.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (loading != null) loading.setVisibility(View.VISIBLE);
                }
            });
            animator.setTarget(loading);
            animator.start();
        } else loading.setVisibility(View.VISIBLE);
    }

    protected void doHideLoading() {
        if (Build.VERSION.SDK_INT > 10) {
            // FIXME: 3.6.16 loading is NULL when activity closed before load
            Animator animator = AnimatorInflater.loadAnimator(loading.getContext(), android.R.animator.fade_out);
            animator.setDuration(loading.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (loading != null) loading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (loading != null) loading.setVisibility(View.GONE);
                }
            });
            animator.setTarget(loading);
            animator.start();
        } else loading.setVisibility(View.GONE);


        if (Build.VERSION.SDK_INT > 10) {
            Animator animator = AnimatorInflater.loadAnimator(content.getContext(), android.R.animator.fade_in);
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (content != null) content.setVisibility(View.VISIBLE);
                }
            });
            animator.setTarget(content);
            animator.start();
        } else content.setVisibility(View.VISIBLE);
    }
}

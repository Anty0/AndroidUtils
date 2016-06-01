package eu.codetopic.utils.activity.loading;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ProgressBar;

import eu.codetopic.utils.simple.SimpleAnimatorListener;
import eu.codetopic.utils.thread.progress.ProgressBarReporter;
import eu.codetopic.utils.thread.progress.ProgressReporter;

public abstract class LoadingViewHolderImpl extends LoadingViewHolder {

    private static final String LOG_TAG = "LoadingViewHolderImpl";

    private ProgressBarReporter progressReporter = null;
    private View loading = null;
    private View content = null;

    public ProgressReporter getProgressReporter() {
        if (progressReporter == null) {
            progressReporter = new ProgressBarReporter(loading instanceof ProgressBar
                    ? (ProgressBar) loading : null);
        }
        return progressReporter;
    }

    @IdRes
    protected abstract int getContentViewId(Context context);

    @IdRes
    protected abstract int getLoadingViewId(Context context);

    @Override
    protected void onUpdateMainView(@Nullable View newMainView) {
        if (newMainView != null) {
            content = newMainView.findViewById(getContentViewId(newMainView.getContext()));
            loading = newMainView.findViewById(getLoadingViewId(newMainView.getContext()));

            if (loading == null || content == null)
                throw new NullPointerException("Used view is not usable for " + LOG_TAG);

            if (progressReporter != null)
                progressReporter.setProgressBar(loading instanceof ProgressBar
                        ? (ProgressBar) loading : null);
        } else {
            content = null;
            loading = null;
            if (progressReporter != null) progressReporter.setProgressBar((ProgressBar) null);
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
                    loading.setVisibility(View.VISIBLE);
                }
            });
            animator.setTarget(loading);
            animator.start();
        } else loading.setVisibility(View.VISIBLE);
    }

    protected void doHideLoading() {
        if (Build.VERSION.SDK_INT > 10) {
            Animator animator = AnimatorInflater.loadAnimator(loading.getContext(), android.R.animator.fade_out);
            animator.setDuration(loading.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    loading.setVisibility(View.GONE);
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
                    content.setVisibility(View.VISIBLE);
                }
            });
            animator.setTarget(content);
            animator.start();
        } else content.setVisibility(View.VISIBLE);
    }
}

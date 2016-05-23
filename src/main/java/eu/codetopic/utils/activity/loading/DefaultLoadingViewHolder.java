package eu.codetopic.utils.activity.loading;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

import eu.codetopic.utils.R;
import eu.codetopic.utils.SimpleAnimatorListener;
import proguard.annotation.Keep;
import proguard.annotation.KeepName;

public class DefaultLoadingViewHolder extends LoadingViewHolder {

    @LayoutRes private static final int LOADING_LAYOUT_ID = R.layout.loading_base;
    @IdRes private static final int CONTENT_VIEW_ID = R.id.base_loadable_content;
    @IdRes private static final int LOADING_VIEW_ID = R.id.base_loading;

    private static final String LOG_TAG = "DefaultLoadingViewHolder";

    private View loading;
    private View content;

    protected DefaultLoadingViewHolder() {
    }

    @Keep
    @KeepName
    private static HolderInfo<DefaultLoadingViewHolder> getHolderInfo() {
        return new HolderInfo<>(DefaultLoadingViewHolder.class, true,
                LOADING_LAYOUT_ID, CONTENT_VIEW_ID);
    }

    @Override
    protected void onUpdateMainView(@Nullable View newMainView) {
        if (newMainView != null) {
            loading = newMainView.findViewById(LOADING_VIEW_ID);
            content = newMainView.findViewById(CONTENT_VIEW_ID);
            if (loading == null || content == null)
                throw new NullPointerException("Used view is not usable for " + LOG_TAG);
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
                    getMainView().requestLayout();
                }
            });
            animator.setTarget(loading);
            animator.start();
        } else loading.setVisibility(View.VISIBLE);
        getMainView().requestLayout();
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
                    getMainView().requestLayout();
                }
            });
            animator.setTarget(content);
            animator.start();
        } else content.setVisibility(View.VISIBLE);
        getMainView().requestLayout();
    }
}

package eu.codetopic.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.UiThread;
import android.view.View;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.simple.SimpleAnimatorListener;

@UiThread
public class AnimationsUtils {

    private static final String LOG_TAG = "AnimationsUtils";
    private static ObjectAnimator fadeOutAnim = null, fadeInAnim = null;

    private AnimationsUtils() {
    }

    private static int getDefaultAnimationDuration(View view) {
        return getDefaultAnimationDuration(view.getContext());
    }

    private static int getDefaultAnimationDuration(Context context) {
        return context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
    }

    public static void cancelAnimationsFor(View view) {
        if (fadeInAnim != null && fadeInAnim.getTarget() == view) fadeInAnim.cancel();
        if (fadeOutAnim != null && fadeOutAnim.getTarget() == view) fadeOutAnim.cancel();
    }

    public static void animateVisibilityChange(View view, boolean visible) {
        animateVisibilityChange(view, visible, getDefaultAnimationDuration(view));
    }

    public static void animateVisibilityChange(View view, boolean visible, int duration) {
        cancelAnimationsFor(view);

        int visibility = view.getVisibility();
        if (visibility == View.INVISIBLE)
            throw new IllegalStateException("Can't animate visibility change from INVISIBLE visibility.");
        if (visible && visibility == View.VISIBLE || !visible && visibility == View.GONE) return;

        if (visible) fadeIn(view, duration);
        else fadeOut(view, duration);
    }

    public static void fadeOut(View view) {
        fadeOut(view, getDefaultAnimationDuration(view));
    }

    public static void fadeOut(View view, int duration) {
        if (fadeOutAnim == null) fadeOutAnim = (ObjectAnimator) AnimatorInflater
                .loadAnimator(view.getContext(), android.R.animator.fade_out);

        final WeakReference<View> viewRef = new WeakReference<>(view);

        fadeOutAnim.setTarget(view);
        fadeOutAnim.setDuration(duration);
        fadeOutAnim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                View view = viewRef.get();
                if (view != null) {
                    view.setAlpha(1f);
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.removeListener(this);
                View view = viewRef.get();
                if (view != null) {
                    view.setAlpha(1f);
                    view.setVisibility(View.GONE);
                }
            }
        });
        fadeOutAnim.start();
    }

    public static void fadeIn(View view) {
        fadeIn(view, getDefaultAnimationDuration(view));
    }

    public static void fadeIn(View view, int duration) {
        if (fadeInAnim == null) fadeInAnim = (ObjectAnimator) AnimatorInflater
                .loadAnimator(view.getContext(), android.R.animator.fade_in);

        final WeakReference<View> viewRef = new WeakReference<>(view);
        fadeInAnim.setTarget(view);
        fadeInAnim.setDuration(duration);
        fadeInAnim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                View view = viewRef.get();
                if (view != null) view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeListener(this);
                View view = viewRef.get();
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                    view.setAlpha(1f);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.removeListener(this);
                View view = viewRef.get();
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                    view.setAlpha(1f);
                }
            }
        });
        fadeInAnim.start();
    }

}

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

package eu.codetopic.utils.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.UiThread;
import android.view.View;

import eu.codetopic.utils.simple.SimpleAnimatorListener;
import eu.codetopic.utils.ui.view.ExtensionsKt;

@UiThread
public class ViewVisibilityAnimator {

    private static final String LOG_TAG = "ViewVisibilityAnimator";
    private static final String VIEW_ANIMATOR_TAG =
            "ViewVisibilityAnimator.VIEW_ANIMATOR_TAG";

    private final Context context;
    private final View view;
    private final ObjectAnimator fadeOutAnim, fadeInAnim;

    private ViewVisibilityAnimator(View targetView) {
        this.context = targetView.getContext();
        this.view = targetView;

        fadeOutAnim = (ObjectAnimator) AnimatorInflater
                .loadAnimator(view.getContext(), android.R.animator.fade_out);
        fadeOutAnim.setTarget(view);
        fadeOutAnim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(1f);
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setAlpha(1f);
                view.setVisibility(View.GONE);
            }
        });


        fadeInAnim = (ObjectAnimator) AnimatorInflater
                .loadAnimator(view.getContext(), android.R.animator.fade_in);
        fadeInAnim.setTarget(view);
        fadeInAnim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(1f);
            }
        });
    }

    public static ViewVisibilityAnimator getAnimatorFor(View view) {
        ViewVisibilityAnimator animator = (ViewVisibilityAnimator)
                ExtensionsKt.getTag(view, VIEW_ANIMATOR_TAG);
        if (animator != null) return animator;
        animator = new ViewVisibilityAnimator(view);
        ExtensionsKt.setTag(view, VIEW_ANIMATOR_TAG, animator);
        return animator;
    }

    private int getDefaultAnimationDuration() {
        return context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
    }

    public void cancelAnimations() {
        fadeInAnim.cancel();
        fadeOutAnim.cancel();
    }

    public void animateVisibilityChange(boolean visible) {
        animateVisibilityChange(visible, getDefaultAnimationDuration());
    }

    public void animateVisibilityChange(boolean visible, int duration) {
        cancelAnimations();

        int visibility = view.getVisibility();
        if (visibility == View.INVISIBLE)
            throw new IllegalStateException("Can't animate visibility change from INVISIBLE visibility.");
        if (visible && visibility == View.VISIBLE || !visible && visibility == View.GONE) return;

        if (visible) fadeIn(duration);
        else fadeOut(duration);
    }

    public void fadeOut() {
        fadeOut(getDefaultAnimationDuration());
    }

    public void fadeOut(int duration) {
        cancelAnimations();
        fadeOutAnim.setDuration(duration);
        fadeOutAnim.start();
    }

    public void fadeIn() {
        fadeIn(getDefaultAnimationDuration());
    }

    public void fadeIn(int duration) {
        cancelAnimations();
        fadeInAnim.setDuration(duration);
        fadeInAnim.start();
    }

}

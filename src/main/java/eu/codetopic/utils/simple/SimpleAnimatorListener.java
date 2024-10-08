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

package eu.codetopic.utils.simple;

import android.animation.Animator;
import android.annotation.TargetApi;

@TargetApi(11)
public class SimpleAnimatorListener implements Animator.AnimatorListener {

    private boolean canceled = false;

    @Override
    public void onAnimationStart(Animator animation) {
        canceled = false;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!canceled) onAnimationNaturalEnd(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        canceled = true;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        canceled = false;
    }

    public void onAnimationNaturalEnd(Animator animation) {
    }
}

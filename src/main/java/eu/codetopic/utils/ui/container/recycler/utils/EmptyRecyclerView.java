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

package eu.codetopic.utils.ui.container.recycler.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import eu.codetopic.utils.ui.animation.ViewVisibilityAnimator;

public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;
    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            updateEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateEmptyView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void updateEmptyView() {
        ViewVisibilityAnimator animator = ViewVisibilityAnimator.getAnimatorFor(this);
        if (mEmptyView != null) {
            Adapter adapter = getAdapter();
            final boolean emptyViewVisible = adapter == null
                    || adapter.getItemCount() == 0;
            if (emptyViewVisible) {
                ViewVisibilityAnimator.getAnimatorFor(mEmptyView)
                        .animateVisibilityChange(true);
            } else {
                ViewVisibilityAnimator.getAnimatorFor(mEmptyView)
                        .cancelAnimations();
                mEmptyView.setVisibility(GONE);
            }
            animator.animateVisibilityChange(!emptyViewVisible);
        } else animator.animateVisibilityChange(true);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        updateEmptyView();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        updateEmptyView();
    }
}
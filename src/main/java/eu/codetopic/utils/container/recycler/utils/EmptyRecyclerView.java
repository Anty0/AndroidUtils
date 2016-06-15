package eu.codetopic.utils.container.recycler.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import eu.codetopic.utils.animation.AnimationsUtils;

public class EmptyRecyclerView extends RecyclerView {

    private View mEmptyView;
    final private AdapterDataObserver observer = new AdapterDataObserver() {
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
            updateEmptyView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            updateEmptyView();
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
        if (mEmptyView != null) {
            Adapter adapter = getAdapter();
            final boolean emptyViewVisible = adapter == null
                    || adapter.getItemCount() == 0;
            AnimationsUtils.animateVisibilityChange(mEmptyView, emptyViewVisible);
            AnimationsUtils.animateVisibilityChange(this, !emptyViewVisible);
        } else AnimationsUtils.animateVisibilityChange(this, true);
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
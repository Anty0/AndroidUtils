package eu.codetopic.utils.container.swipe;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class SwipeLayout {

    private static final String LOG_TAG = "SwipeLayout";

    public static SwipeLayoutInflaterImpl inflate() {
        return new SwipeLayoutInflaterImpl();
    }

    public static final class SwipeLayoutInflaterImpl extends SwipeLayoutInflater<SwipeLayoutInflaterImpl, SwipeLayoutManagerImpl> {

        private SwipeLayoutInflaterImpl() {
        }

        @Override
        protected SwipeLayoutInflaterImpl self() {
            return this;
        }

        @Override
        protected SwipeLayoutManagerImpl getManagerInstance(View view) {
            return new SwipeLayoutManagerImpl(view, getSchemeColors(),
                    isUseSwipeRefresh(), isUseFloatingActionButton());
        }
    }

    public static final class SwipeLayoutManagerImpl extends SwipeLayoutManager<SwipeLayoutManagerImpl> {

        private SwipeLayoutManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                       boolean useSwipeRefresh, boolean useFloatingActionButton) {
            super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        }

        @Override
        protected SwipeLayoutManagerImpl self() {
            return this;
        }
    }
}

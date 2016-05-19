package eu.codetopic.utils.container.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class Recycler {

    private static final String LOG_TAG = "Recycler";

    public static RecyclerInflaterImpl inflate() {
        return new RecyclerInflaterImpl();
    }

    public static final class RecyclerInflaterImpl extends
            RecyclerInflater<RecyclerInflaterImpl, RecyclerManagerImpl> {

        private RecyclerInflaterImpl() {
        }

        @Override
        protected RecyclerInflaterImpl self() {
            return this;
        }

        @Override
        protected RecyclerManagerImpl getManagerInstance(View view) {
            return new RecyclerManagerImpl(view, getSchemeColors(),
                    isUseSwipeRefresh(), isUseFloatingActionButton());
        }
    }

    public static final class RecyclerManagerImpl extends RecyclerManager<RecyclerManagerImpl> {

        protected RecyclerManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                      boolean useSwipeRefresh, boolean useFloatingActionButton) {

            super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        }

        @Override
        protected RecyclerManagerImpl self() {
            return this;
        }
    }

}

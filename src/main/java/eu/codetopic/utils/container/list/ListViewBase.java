package eu.codetopic.utils.container.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class ListViewBase {

    private static final String LOG_TAG = "ListViewBase";

    public static ListViewInflaterImpl inflate() {
        return new ListViewInflaterImpl();
    }

    public static final class ListViewInflaterImpl extends
            ListViewInflater<ListViewInflaterImpl, ListViewManagerImpl> {

        private ListViewInflaterImpl() {
        }

        @Override
        protected ListViewInflaterImpl self() {
            return this;
        }

        @Override
        protected ListViewManagerImpl getManagerInstance(View view) {
            return new ListViewManagerImpl(view, getSchemeColors(),
                    isUseSwipeRefresh(), isUseFloatingActionButton());
        }
    }

    public static final class ListViewManagerImpl extends ListViewManager<ListViewManagerImpl> {

        protected ListViewManagerImpl(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                                      boolean useSwipeRefresh, boolean useFloatingActionButton) {

            super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        }

        @Override
        protected ListViewManagerImpl self() {
            return this;
        }
    }

}

package eu.codetopic.utils.activity.loading;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 31.3.16.
 *
 * @author anty
 */
public abstract class LoadingViewHolder {

    @LayoutRes public static final int DEFAULT_LOADING_LAYOUT_ID = R.layout.loading_base;
    @IdRes public static final int DEFAULT_LOADING_VIEW_ID = R.id.base_loading;
    @IdRes public static final int DEFAULT_CONTENT_VIEW_ID = R.id.base_loadable_content;

    private final Object lock = new Object();
    private View mainView;
    private int loadingDepth = 0;

    protected LoadingViewHolder() {
    }

    final void updateViews(@Nullable View mainView) {
        synchronized (lock) {
            clearViews();
            if (mainView == null) return;
            this.mainView = mainView;
            try {
                onUpdateMainView(mainView);
                if (loadingDepth > 0) doShowLoading();
            } catch (Throwable t) {
                this.mainView = null;
                onUpdateMainView(null);
                throw t;
            }
        }
    }

    final void clearViews() {
        synchronized (lock) {
            if (loadingDepth > 0) doHideLoading();
            mainView = null;
            onUpdateMainView(null);
        }
    }

    protected abstract void onUpdateMainView(@Nullable View newMainView);

    public View getMainView() {
        return mainView;
    }

    public boolean hasAttachedView() {
        return mainView != null;
    }

    public final void showLoading() {
        synchronized (lock) {
            if (loadingDepth == 0)
                JobUtils.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            doShowLoading();
                        }
                    }
                });
            loadingDepth++;
        }
    }

    protected abstract void doShowLoading();

    public final void hideLoading() {
        synchronized (lock) {
            loadingDepth--;
            if (loadingDepth == 0)
                JobUtils.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            doHideLoading();
                        }
                    }
                });
        }
    }

    protected abstract void doHideLoading();
}

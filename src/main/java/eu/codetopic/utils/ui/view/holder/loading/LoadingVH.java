package eu.codetopic.utils.ui.view.holder.loading;

import android.content.Context;
import android.support.annotation.UiThread;
import android.view.View;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.ui.view.holder.ViewHolder;

public abstract class LoadingVH extends ViewHolder {

    private static final String LOG_TAG = "LoadingVH";

    private int loadingDepth = 0;

    @UiThread
    @Override
    protected void onViewUpdated() {
        super.onViewUpdated();

        if (loadingDepth == 0) doHideLoading();
        else doShowLoading();
    }

    private Context getViewContext() {
        View view = getView();
        return view == null ? null : view.getContext();
    }

    public final void showLoading() {
        synchronized (getViewLock()) {
            if (loadingDepth == 0) {
                JobUtils.postOnContextThread(getViewContext(), new Runnable() {
                    @Override
                    public void run() {
                        synchronized (getViewLock()) {
                            doShowLoading();
                        }
                    }
                });
            }
            loadingDepth++;
        }
    }

    @UiThread
    protected abstract void doShowLoading();

    public final void hideLoading() {
        synchronized (getViewLock()) {
            loadingDepth--;
            if (loadingDepth == 0) {
                JobUtils.postOnContextThread(getViewContext(), new Runnable() {
                    @Override
                    public void run() {
                        synchronized (getViewLock()) {
                            doHideLoading();
                        }
                    }
                });
            }
            if (loadingDepth < 0)
                Log.e(LOG_TAG, "hideLoading: Called hideLoading()" +
                        " without calling showLoading() before.");
        }
    }

    @UiThread
    protected abstract void doHideLoading();

    public final boolean isLoadingShowed() {
        synchronized (getViewLock()) {
            return loadingDepth > 0;
        }
    }
}

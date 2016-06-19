package eu.codetopic.utils.view.holder.loading;

import android.support.annotation.UiThread;

import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.view.holder.ViewHolder;

public abstract class LoadingVH extends ViewHolder {

    private static final String LOG_TAG = "LoadingVH";

    private int loadingDepth = 0;

    public final void showLoading() {
        synchronized (getViewLock()) {
            if (loadingDepth == 0)
                JobUtils.postOnViewThread(getView(), new Runnable() {
                    @Override
                    public void run() {
                        synchronized (getViewLock()) {
                            doShowLoading();
                        }
                    }
                });
            loadingDepth++;
        }
    }

    @UiThread
    protected abstract void doShowLoading();

    public final void hideLoading() {
        synchronized (getViewLock()) {
            loadingDepth--;
            if (loadingDepth == 0) {
                JobUtils.postOnViewThread(getView(), new Runnable() {
                    @Override
                    public void run() {
                        synchronized (getViewLock()) {
                            doHideLoading();
                        }
                    }
                });
            }
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

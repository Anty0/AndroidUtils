package eu.codetopic.utils.thread.job;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.view.holder.loading.LoadingVH;

public abstract class LoadingJob extends Job {

    private static final String LOG_TAG = "LoadingJob";

    private final LoadingVH mLoadingViewHolder;
    private boolean mLoadingShowed = false;

    protected LoadingJob(Params params, @Nullable LoadingVH loadingViewHolder) {
        super(params);
        mLoadingViewHolder = loadingViewHolder;
    }

    public LoadingVH getViewHolder() {
        return mLoadingViewHolder;
    }

    @Override
    public void onAdded() {
        if (!mLoadingShowed && mLoadingViewHolder != null) {
            mLoadingViewHolder.showLoading();
            mLoadingShowed = true;
        }
    }

    @Override
    public final void onRun() throws Throwable {
        try {
            onStart();
            hideLoading();
        } catch (Throwable t) {
            Log.d(LOG_TAG, "onRun", t);
            throw t;
        }
    }

    @WorkerThread
    public abstract void onStart() throws Throwable;

    @Override
    protected int getRetryLimit() {
        return Constants.JOB_REPEAT_COUNT_DEFAULT;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return new RetryConstraint(true);
    }

    @Override
    protected void onCancel(@CancelReason int cancelReason) {
        hideLoading();
    }

    private void hideLoading() {
        if (mLoadingShowed && mLoadingViewHolder != null) {
            mLoadingViewHolder.hideLoading();
            mLoadingShowed = false;
        }
    }
}

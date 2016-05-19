package eu.codetopic.utils.thread.job;

import android.support.annotation.Nullable;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;

public abstract class LoadingJob extends Job {

    private final LoadingViewHolder mLoadingViewHolder;
    private boolean mLoadingShowed = false;

    protected LoadingJob(Params params, @Nullable LoadingViewHolder loadingViewHolder) {
        super(params);
        mLoadingViewHolder = loadingViewHolder;
    }

    public LoadingViewHolder getViewHolder() {
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
        onStart();
        hideLoading();
    }

    public abstract void onStart() throws Throwable;

    @Override
    protected int getRetryLimit() {
        return Constants.JOB_REPEAT_COUNT_DEFAULT;
    }

    @Override
    protected void onCancel() {
        hideLoading();
    }

    private void hideLoading() {
        if (mLoadingShowed && mLoadingViewHolder != null) {
            mLoadingViewHolder.hideLoading();
            mLoadingShowed = false;
        }
    }
}

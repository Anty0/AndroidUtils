package eu.codetopic.utils.thread.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.Constants;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

public abstract class LoadingJob extends Job implements Serializable {

    private static final String LOG_TAG = "LoadingJob";

    private final LoadingVH mLoadingViewHolder;
    private boolean mLoadingShowed = false;

    protected LoadingJob(Params params, @Nullable LoadingVH loadingViewHolder) {
        super(params);
        mLoadingViewHolder = loadingViewHolder;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
    }


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
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
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.RETRY;
    }

    @Override
    protected void onCancel(@CancelReason int cancelReason, Throwable throwable) {
        hideLoading();
    }

    private void hideLoading() {
        if (mLoadingShowed && mLoadingViewHolder != null) {
            mLoadingViewHolder.hideLoading();
            mLoadingShowed = false;
        }
    }
}

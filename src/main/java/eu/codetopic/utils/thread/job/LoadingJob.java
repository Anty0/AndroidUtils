/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

package com.codetopic.utils.thread.service;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.RetryConstraint;
import com.codetopic.utils.Log;
import com.codetopic.utils.thread.JobUtils;
import com.codetopic.utils.thread.progress.ProgressReporter;

class Work extends Job {

    private static final String LOG_TAG = "Work";

    private final Context mContext;
    private final WorkInfo mWorkInfo;
    private boolean mShowingNotification = false;

    Work(Context context, WorkInfo work) {
        super(work.getWork().getJobParams(context).setPersistent(false));
        mContext = context;
        mWorkInfo = work;
    }

    @Override
    public void onAdded() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                stopProgress();
                mShowingNotification = true;
                ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(mWorkInfo.getNotificationId(), mWorkInfo.getNotification().build());
            }
        });
    }

    @Override
    protected int getRetryLimit() {
        ServiceWork.RetryLimit limit = mWorkInfo.getWork().getClass()
                .getAnnotation(ServiceWork.RetryLimit.class);
        return limit != null ? limit.value() : super.getRetryLimit();
    }

    @Override
    public void onRun() throws Throwable {
        try {
            mWorkInfo.getWork().run(mContext, mWorkInfo.getProgressReporter());
            stopProgress();
        } catch (Throwable t) {
            Log.d(LOG_TAG, "shouldReRunOnThrowable", t);
            throw t;
        }
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return new RetryConstraint(true);
    }

    @Override
    protected void onCancel(@CancelReason int cancelReason, Throwable throwable) {
        stopProgress();
    }

    private void stopProgress() {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mShowingNotification) {
                    mShowingNotification = false;
                    ProgressReporter reporter = mWorkInfo.getProgressReporter();
                    if (reporter != null) reporter.stopShowingProgress();
                    mWorkInfo.onBeforeNotificationRemoved();
                    ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE))
                            .cancel(mWorkInfo.getNotificationId());
                }
            }
        });
    }
}

package eu.codetopic.utils.thread.service;

import android.app.NotificationManager;
import android.content.Context;

import com.path.android.jobqueue.Job;

import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.ProgressReporter;

/**
 * Created by anty on 25.3.16.
 *
 * @author anty
 */
class Work extends Job {

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
        mWorkInfo.getWork().run(mContext, mWorkInfo.getProgressReporter());
        stopProgress();
    }

    @Override
    protected void onCancel() {
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

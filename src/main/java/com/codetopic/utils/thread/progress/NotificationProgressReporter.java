package com.codetopic.utils.thread.progress;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.codetopic.utils.thread.JobUtils;

public class NotificationProgressReporter extends ProgressReporterImpl {

    private final int mId;
    private final Context mContext;
    private final NotificationCompat.Builder mNotification;

    public NotificationProgressReporter(Context context, NotificationCompat.Builder notification, int id) {
        mId = id;
        mContext = context.getApplicationContext();
        mNotification = notification;
        mNotification.setOngoing(true).setOnlyAlertOnce(true)
                .setProgress(getMax(), getProgress(), isIntermediate());
    }

    public int getId() {
        return mId;
    }

    public Context getContext() {
        return mContext;
    }

    public NotificationCompat.Builder getNotification() {
        return mNotification;
    }

    @Override
    protected void onChange(ProgressInfo info) {
        mNotification.setProgress(info.getMaxProgress(),
                info.getProgress(), info.isIntermediate());

        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((NotificationManager) mContext.getSystemService(Context
                        .NOTIFICATION_SERVICE)).notify(mId, mNotification.build());
            }
        });
    }
}

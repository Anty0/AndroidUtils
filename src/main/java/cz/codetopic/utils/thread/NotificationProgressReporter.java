package cz.codetopic.utils.thread;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by anty on 13.3.16.
 *
 * @author anty
 */
public class NotificationProgressReporter implements ProgressReporter {

    private final int mId;
    private final Context mContext;
    private final NotificationCompat.Builder mNotification;

    private boolean showingProgress = false;
    private int max = 100;
    private int progress = 0;
    private boolean intermediate = true;

    public NotificationProgressReporter(Context context, NotificationCompat.Builder notification, int id) {
        mId = id;
        mContext = context;
        mNotification = notification;
        mNotification.setOngoing(true).setOnlyAlertOnce(true)
                .setProgress(max, progress, intermediate);
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
    public synchronized void startShowingProgress() {
        showingProgress = true;
        max = 100;
        progress = 0;
        intermediate = false;
        updateNotification();
    }

    @Override
    public synchronized void stopShowingProgress() {
        max = 100;
        progress = 0;
        intermediate = true;
        updateNotification();
        showingProgress = false;
    }

    @Override
    public synchronized void setIntermediate(boolean intermediate) {
        this.intermediate = intermediate;
        updateNotification();
    }

    @Override
    public synchronized void setMaxProgress(int max) {
        this.max = max;
        intermediate = false;
        updateNotification();
    }

    @Override
    public synchronized void reportProgress(int progress) {
        this.progress = progress;
        intermediate = false;
        updateNotification();
    }

    @Override
    public void stepProgress(int step) {
        reportProgress(progress + step);
    }

    @Override
    public ProgressInfo getProgressInfo() {
        return new ProgressInfoImpl(max, progress, intermediate);
    }

    private synchronized void updateNotification() {
        if (!showingProgress) return;
        mNotification.setProgress(max, progress, intermediate);
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((NotificationManager) mContext.getSystemService(Context
                        .NOTIFICATION_SERVICE)).notify(mId, mNotification.build());
            }
        });
    }
}

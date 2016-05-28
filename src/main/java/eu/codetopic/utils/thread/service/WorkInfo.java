package eu.codetopic.utils.thread.service;

import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.birbit.android.jobqueue.JobManager;

import eu.codetopic.utils.thread.NotificationProgressReporter;
import eu.codetopic.utils.thread.ProgressInfo;
import eu.codetopic.utils.thread.ProgressReporter;

public abstract class WorkInfo {

    private final int notificationId;
    private final NotificationCompat.Builder notification;
    private final ProgressReporter progressReporter;
    private final boolean hasProgress;
    private final JobManager jobManager;
    private final ServiceWork work;
    private String workId = null;

    WorkInfo(Context context, JobManager jobManager, int notificationId, ServiceWork work) {
        this(notificationId, new NotificationCompat.Builder(context), jobManager, work);
    }

    WorkInfo(int notificationId, NotificationCompat.Builder notification, JobManager jobManager, ServiceWork work) {
        this.notificationId = notificationId;
        ServiceWork.UseProgress useProgress = work.getClass()
                .getAnnotation(ServiceWork.UseProgress.class);
        this.hasProgress = useProgress == null || useProgress.value();
        notification.extend(work.getNotificationExtender(notification.mContext));
        this.progressReporter = hasProgress ? new NotificationProgressReporter
                (notification.mContext, notification, notificationId) : null;
        this.notification = notification;
        this.jobManager = jobManager;
        this.work = work;
    }

    public String getWorkId() {
        return workId;
    }

    void setWorkId(String id) {
        this.workId = id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public NotificationCompat.Builder getNotification() {
        return notification;
    }

    public boolean hasProgressNotification() {
        return hasProgress;
    }

    public ProgressReporter getProgressReporter() {
        return progressReporter;
    }

    public ProgressInfo getProgressInfo() {
        return progressReporter != null ? progressReporter.getProgressInfo() : null;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public ServiceWork getWork() {
        return work;
    }

    abstract void onBeforeNotificationRemoved();
}

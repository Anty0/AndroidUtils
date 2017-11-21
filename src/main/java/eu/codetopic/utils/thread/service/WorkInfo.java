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

package eu.codetopic.utils.thread.service;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.birbit.android.jobqueue.JobManager;

import eu.codetopic.utils.thread.progress.NotificationProgressReporter;
import eu.codetopic.utils.thread.progress.ProgressInfo;
import eu.codetopic.utils.thread.progress.ProgressReporter;

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

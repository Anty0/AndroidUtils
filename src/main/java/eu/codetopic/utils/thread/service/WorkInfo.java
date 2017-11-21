/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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

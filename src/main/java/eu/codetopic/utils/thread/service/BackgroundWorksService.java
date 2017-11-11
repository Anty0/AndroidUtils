/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.thread.service;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.JobStatus;

import java.util.ArrayList;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.utils.data.getter.JobManagerGetter;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.service.CommandService;

public final class BackgroundWorksService extends CommandService<BackgroundWorksService.WorkBinder> {
    // TODO: 13.8.16 add support for dialog works

    private static final String LOG_TAG = "BackgroundWorksService";
    private final ArrayList<WorkInfo> works = new ArrayList<>();
    private int actualForegroundId = -1;
    private boolean stopped = false;

    private WorkInfo execute(JobManager jobManager, ServiceWork work) {
        if (jobManager == null || work == null)
            throw new NullPointerException("jobManager and work mustn't be null");

        synchronized (works) {
            if (isStopped()) throw new IllegalStateException(LOG_TAG + " is stopped (what?)");

            WorkInfo info = new WorkInfo(this, jobManager, Identifiers
                    .next(Identifiers.TYPE_NOTIFICATION_ID), work) {
                @Override
                public void onBeforeNotificationRemoved() {
                    synchronized (works) {
                        if (works.remove(this) && getNotificationId() == actualForegroundId) {
                            stopForeground(false);
                            actualForegroundId = -1;
                            findNewForegroundNotification();
                        }
                    }
                    if (!isRunning()) safeStopSelf();
                }
            };
            Work job = new Work(this, info);
            info.setWorkId(job.getId());
            jobManager.addJobInBackground(job);
            works.add(info);
            if (actualForegroundId == -1) findNewForegroundNotification();
            return info;
        }
    }

    private void findNewForegroundNotification() {
        synchronized (works) {
            for (WorkInfo info : works) {
                if (info.getJobManager().getJobStatus(info.getWorkId())
                        == JobStatus.UNKNOWN) continue;
                actualForegroundId = info.getNotificationId();
                startForeground(info.getNotificationId(),
                        info.getNotification().build());
                return;
            }
        }
    }

    private WorkInfo getWork(String id) {
        synchronized (works) {
            for (WorkInfo work : works) {
                if (Objects.equals(work.getWorkId(), id))
                    return work;
            }
        }
        return null;
    }

    private boolean isRunning() {
        synchronized (works) {
            return !works.isEmpty();
        }
    }

    private boolean isStopped() {
        synchronized (works) {
            return stopped;
        }
    }

    private void safeStopSelf() {
        synchronized (works) {
            stopSelf();
            stopped = true;
        }
    }

    @Nullable
    @Override
    public WorkBinder onBind(Intent intent) {
        return new WorkBinder();
    }

    public final class WorkBinder extends CommandService.CommandBinder {

        private WorkBinder() {
        }

        /**
         * Start work on jobManager and show progress notification
         *
         * @param jobManagerGetter JobManager to run work
         * @param work             work to run
         * @return info for created work
         */
        public WorkInfo startWork(JobManagerGetter jobManagerGetter, ServiceWork work) {
            return startWork(jobManagerGetter.getJobManager(), work);
        }

        /**
         * Start work on jobManager and show progress notification
         *
         * @param jobManager JobManager to run work
         * @param work       work to run
         * @return info for created work
         */
        public WorkInfo startWork(JobManager jobManager, ServiceWork work) {
            return execute(jobManager, work);
        }

        /**
         * Find work with same work id as given workId
         *
         * @param workId id of work
         * @return found work info
         */
        public WorkInfo findWorkById(String workId) {
            return getWork(workId);
        }

        /**
         * Check any work running on BackgroundWorksService
         *
         * @return true if any work running on BackgroundWorksService
         */
        public boolean isRunning() {
            return BackgroundWorksService.this.isRunning();
        }

        /**
         * Check if BackgroundWorksService is stopped
         *
         * @return true if BackgroundWorksService is stopped
         */
        public boolean isStopped() {
            return BackgroundWorksService.this.isStopped();
        }

        @Override
        public boolean isUnneeded() {
            return !isRunning();
        }
    }

}
package eu.codetopic.utils.thread.service;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.JobStatus;

import java.util.ArrayList;

import eu.codetopic.utils.exceptions.NoModuleFoundException;
import eu.codetopic.utils.notifications.manage.NotificationIdsManager;
import eu.codetopic.utils.service.CommandService;

public final class BackgroundWorksService extends CommandService<BackgroundWorksService.WorkBinder> {

    private static final String LOG_TAG = "BackgroundWorksService";
    private final ArrayList<WorkInfo> works = new ArrayList<>();
    private int actualForegroundId = -1;
    private boolean stopped = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (NotificationIdsManager.getInstance() == null) {
            safeStopSelf();
            throw new NoModuleFoundException("NotificationIdsManager no found please add it to ModulesManager initialization");
        }
    }

    private WorkInfo execute(JobManager jobManager, ServiceWork work) {
        if (jobManager == null || work == null)
            throw new NullPointerException("jobManager and work mustn't be null");

        synchronized (works) {
            if (isStopped()) throw new IllegalStateException(LOG_TAG + " is stopped (what?)");

            WorkInfo info = new WorkInfo(this, jobManager, NotificationIdsManager.getInstance()
                    .obtainNewId(new WorksIdsGroup()), work) {
                @Override
                public void onBeforeNotificationRemoved() {
                    synchronized (works) {
                        if (works.remove(this)) {
                            NotificationIdsManager.getInstance().notifyIdRemoved(
                                    new WorksIdsGroup(), getNotificationId());
                            if (getNotificationId() == actualForegroundId) {
                                stopForeground(false);
                                actualForegroundId = -1;
                                findNewForegroundNotification();
                            }
                        }
                    }
                    if (!isRunning()) safeStopSelf();
                }
            };
            info.setWorkId(jobManager.addJob(new Work(this, info)));
            works.add(info);
            if (actualForegroundId == -1) findNewForegroundNotification();
            return info;
        }
    }

    private void findNewForegroundNotification() {
        synchronized (works) {
            for (WorkInfo info : works) {
                if (info.getJobManager().getJobStatus(info.getWorkId(),
                        false) == JobStatus.UNKNOWN) continue;
                actualForegroundId = info.getNotificationId();
                startForeground(info.getNotificationId(),
                        info.getNotification().build());
                return;
            }
        }
    }

    private WorkInfo getWork(long id) {
        synchronized (works) {
            for (WorkInfo work : works) {
                if (work.getWorkId() == id)
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
        public WorkInfo findWorkById(long workId) {
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
package eu.codetopic.utils.thread.job;

import com.path.android.jobqueue.JobManager;

import eu.codetopic.utils.data.getter.JobManagerGetter;

public class SingletonJobManagerGetter implements JobManagerGetter {

    @Override
    public JobManager getJobManager() {
        return SingletonJobManager.getInstance();
    }
}

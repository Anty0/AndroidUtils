package com.codetopic.utils.thread.job;

import com.birbit.android.jobqueue.JobManager;
import com.codetopic.utils.data.getter.JobManagerGetter;

public class SingletonJobManagerGetter implements JobManagerGetter {

    @Override
    public JobManager getJobManager() {
        return SingletonJobManager.getInstance();
    }
}

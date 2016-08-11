package com.codetopic.utils.data.getter;

import com.birbit.android.jobqueue.JobManager;

public interface JobManagerGetter extends BaseGetter {

    JobManager getJobManager();
}

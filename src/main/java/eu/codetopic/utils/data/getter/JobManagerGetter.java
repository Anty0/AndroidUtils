package eu.codetopic.utils.data.getter;

import com.path.android.jobqueue.JobManager;

public interface JobManagerGetter extends BaseGetter {

    JobManager getJobManager();
}

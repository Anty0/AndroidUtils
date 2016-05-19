package eu.codetopic.utils.thread.job;

import com.path.android.jobqueue.JobManager;

import eu.codetopic.utils.data.getter.JobManagerGetter;

/**
 * Created by anty on 15.5.16.
 *
 * @author anty
 */
public class SingletonJobManagerGetter implements JobManagerGetter {

    @Override
    public JobManager getJobManager() {
        return SingletonJobManager.getInstance();
    }
}

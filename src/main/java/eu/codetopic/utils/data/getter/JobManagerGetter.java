package eu.codetopic.utils.data.getter;

import com.path.android.jobqueue.JobManager;

/**
 * Created by anty on 15.5.16.
 *
 * @author anty
 */
public interface JobManagerGetter extends BaseGetter {

    JobManager getJobManager();
}

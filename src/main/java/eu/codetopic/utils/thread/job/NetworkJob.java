package eu.codetopic.utils.thread.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.RetryConstraint;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.data.getter.JobManagerGetter;

public class NetworkJob extends LoadingJob {

    private static final String LOG_TAG = "NetworkJob";
    private static final String JOB_NETWORK_GROUP_NAME_ADD = ".NETWORK_GROUP";

    private final NetworkWork job;

    protected NetworkJob(@Nullable LoadingViewHolder loadingViewHolder, @Nullable Class<?> syncObj) {
        this(loadingViewHolder, syncObj, null);
    }

    public NetworkJob(@Nullable Class<?> syncObj, @Nullable NetworkWork work) {
        this(null, syncObj, work);
    }

    public NetworkJob(@Nullable LoadingViewHolder loadingViewHolder,
                      @Nullable Class<?> syncObj, @Nullable NetworkWork work) {
        super(generateParams(syncObj), loadingViewHolder);
        job = work;
    }

    public static long start(@NonNull JobManagerGetter jobManagerGetter, @NonNull NetworkJob job) {
        return start(jobManagerGetter.getJobManager(), job);
    }

    public static long start(@NonNull JobManager jobManager, @NonNull NetworkJob job) {
        return jobManager.addJob(job);
    }

    public static String generateNetworkJobGroupNameFor(Class<?> syncObj) {
        return syncObj.getName() + JOB_NETWORK_GROUP_NAME_ADD;
    }

    private static Params generateParams(@Nullable Class<?> syncObj) {
        Params params = new Params(Constants.JOB_PRIORITY_NETWORK).requireNetwork();
        if (syncObj != null) params.groupBy(generateNetworkJobGroupNameFor(syncObj));
        return params;
    }

    @Override
    public void onStart() throws Throwable {
        if (job != null) job.run();
    }

    @Override
    protected int getRetryLimit() {
        return getViewHolder() == null ? super.getRetryLimit() : Constants.JOB_REPEAT_COUNT_NETWORK;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        Log.e(LOG_TAG, "shouldReRunOnThrowable", throwable);
        return super.shouldReRunOnThrowable(throwable, runCount, maxRunCount);
    }

    public interface NetworkWork {

        @WorkerThread
        void run() throws Throwable;
    }
}

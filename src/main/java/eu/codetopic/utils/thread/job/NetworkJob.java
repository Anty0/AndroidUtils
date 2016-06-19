package eu.codetopic.utils.thread.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.data.getter.JobManagerGetter;
import eu.codetopic.utils.view.holder.loading.LoadingVH;

public class NetworkJob extends LoadingJob {

    private static final String LOG_TAG = "NetworkJob";
    private static final String JOB_NETWORK_GROUP_NAME_ADD = ".NETWORK_GROUP";

    private final NetworkWork job;

    protected NetworkJob(@Nullable LoadingVH loadingViewHolder, @Nullable Class<?> syncObj) {
        this(loadingViewHolder, syncObj, null);
    }

    public NetworkJob(@Nullable Class<?> syncObj, @Nullable NetworkWork work) {
        this(null, syncObj, work);
    }

    public NetworkJob(@Nullable LoadingVH loadingViewHolder,
                      @Nullable Class<?> syncObj, @Nullable NetworkWork work) {
        super(generateParams(syncObj), loadingViewHolder);
        job = work;
    }

    public static String start(@NonNull JobManagerGetter jobManagerGetter, @NonNull NetworkJob job) {
        return start(jobManagerGetter.getJobManager(), job);
    }

    public static String start(@NonNull JobManager jobManager, @NonNull NetworkJob job) {
        jobManager.addJobInBackground(job);
        return job.getId();
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

    public interface NetworkWork {

        @WorkerThread
        void run() throws Throwable;
    }
}

package eu.codetopic.utils.thread.job;

import android.support.annotation.Nullable;

import com.path.android.jobqueue.Params;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;

/**
 * Created by anty on 26.2.16.
 *
 * @author anty
 */
public abstract class NetworkJob extends LoadingViewHolderJob {

    private static final String JOB_NETWORK_GROUP_NAME_ADD = ".NETWORK_GROUP";

    protected NetworkJob(@Nullable LoadingViewHolder loadingViewHolder, Class syncObj) {
        super(new Params(Constants.JOB_PRIORITY_NETWORK).requireNetwork()
                .groupBy(generateNetworkJobGroupNameFor(syncObj)), loadingViewHolder);
    }

    public static String generateNetworkJobGroupNameFor(Class syncObj) {
        return syncObj.getName() + JOB_NETWORK_GROUP_NAME_ADD;
    }

    @Override
    protected int getRetryLimit() {
        return getViewHolder() == null ? super.getRetryLimit() : Constants.JOB_REPEAT_COUNT_NETWORK;
    }
}

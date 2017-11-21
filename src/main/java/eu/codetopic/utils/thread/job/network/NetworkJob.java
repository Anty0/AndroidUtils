/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.thread.job.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.data.getter.JobManagerGetter;
import eu.codetopic.utils.thread.job.LoadingJob;
import eu.codetopic.utils.thread.job.SingletonJobManager;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

public class NetworkJob extends LoadingJob {

    protected static final String LOG_TAG = "NetworkJob";
    private static final String JOB_NETWORK_GROUP_NAME_ADD = ".NETWORK_GROUP";

    @Nullable
    private final Work job;

    protected NetworkJob(@Nullable LoadingVH loadingViewHolder, @Nullable Class<?> syncCls) {
        this(loadingViewHolder, syncCls, null);
    }

    public NetworkJob(@Nullable Class<?> syncCls, @Nullable Work work) {
        this(null, syncCls, work);
    }

    public NetworkJob(@Nullable LoadingVH loadingViewHolder,
                      @Nullable Class<?> syncCls, @Nullable Work work) {
        super(generateParams(syncCls), loadingViewHolder);
        job = work;
    }

    public static String start(@NonNull Work work) {
        return start(null, work);
    }

    public static String start(@Nullable LoadingVH loadingViewHolder, @NonNull Work work) {
        return start(SingletonJobManager.getter,
                new NetworkJob(loadingViewHolder, NetworkJob.class, work));
    }

    public static String start(@NonNull JobManagerGetter jobManagerGetter, @NonNull NetworkJob job) {
        return start(jobManagerGetter.getJobManager(), job);
    }

    public static String start(@NonNull JobManager jobManager, @NonNull NetworkJob job) {
        jobManager.addJobInBackground(job);
        return job.getId();
    }

    public static String generateNetworkJobGroupNameFor(Class<?> syncCls) {
        return syncCls.getName() + JOB_NETWORK_GROUP_NAME_ADD;
    }

    private static Params generateParams(@Nullable Class<?> syncCls) {
        Params params = new Params(Constants.JOB_PRIORITY_NETWORK);// TODO: 24.7.16 add way to specify if this work requires network
        if (syncCls != null) params.groupBy(generateNetworkJobGroupNameFor(syncCls));
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

    public interface Work {

        @WorkerThread
        void run() throws Throwable;
    }

}

/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

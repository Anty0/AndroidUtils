package com.codetopic.utils.thread.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.codetopic.utils.data.getter.JobManagerGetter;

public final class SingletonJobManager {

    public static final JobManagerGetter getter = new SingletonJobManagerGetter();
    private static final String LOG_TAG = "SingletonJobManager";

    private static JobManager mInstance = null;

    private SingletonJobManager() {
    }

    public static void initialize(@NonNull Context context) {
        context = context.getApplicationContext();
        initialize(new JobManager(new Configuration.Builder(context).id(LOG_TAG).build()));
    }

    public static void initialize(@NonNull JobManager jobManagerInstance) {
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = jobManagerInstance;
    }

    public static boolean isInitialized() {
        return mInstance != null;
    }

    public static JobManager getInstance() {
        return mInstance;
    }

    /**
     * Use constant SingletonJobManager.getter instead
     *
     * @return getter for SingletonJobManager
     */
    @Deprecated
    public static JobManagerGetter getGetter() {
        return getter;
    }

}

package eu.codetopic.utils.thread.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.path.android.jobqueue.JobManager;

import eu.codetopic.utils.data.getter.JobManagerGetter;

/**
 * Created by anty on 15.5.16.
 *
 * @author anty
 */
public final class SingletonJobManager {

    public static final JobManagerGetter getter = new SingletonJobManagerGetter();
    private static final String LOG_TAG = "SingletonJobManager";

    private static JobManager mInstance = null;

    private SingletonJobManager() {
    }

    public static void initialize(@NonNull Context context) {
        context = context.getApplicationContext();
        initialize(new JobManager(context, context.getPackageName() + ".JOB_MANAGER"));
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

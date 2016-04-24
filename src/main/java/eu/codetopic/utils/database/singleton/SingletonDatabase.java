package eu.codetopic.utils.database.singleton;

import android.content.Context;
import android.support.annotation.NonNull;

import com.path.android.jobqueue.JobManager;

import eu.codetopic.utils.database.DatabaseBase;
import eu.codetopic.utils.module.getter.DatabaseDaoGetter;

/**
 * Created by anty on 24.4.16.
 *
 * @author anty
 */
public class SingletonDatabase {

    private static final String LOG_TAG = "SingletonDatabase";

    private static DatabaseBase mInstance = null;
    private static JobManager mJobManager = null;

    public static void initialize(@NonNull Context context, @NonNull DatabaseBase databaseInstance) {
        initialize(databaseInstance, new JobManager(context, context.getPackageName() + ".JOB_MANAGER"));
    }

    public static void initialize(@NonNull DatabaseBase database, @NonNull JobManager jobManager) {
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = database;
        mJobManager = jobManager;
    }

    public static DatabaseBase getInstance() {
        return mInstance;
    }

    public static JobManager getJobManager() {
        return mJobManager;
    }

    public static <DT> DatabaseDaoGetter<DT> getGetterFor(Class<DT> dataClass) {
        return new SingletonDatabaseDaoGetter<>(dataClass);
    }
}

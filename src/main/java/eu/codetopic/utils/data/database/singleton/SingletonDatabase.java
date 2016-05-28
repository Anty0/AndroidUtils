package eu.codetopic.utils.data.database.singleton;

import android.support.annotation.NonNull;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.SingletonJobManager;

public final class SingletonDatabase {

    private static final String LOG_TAG = "SingletonDatabase";

    private static DatabaseBase mInstance = null;

    private SingletonDatabase() {
    }

    public static void initialize(@NonNull DatabaseBase database) {
        if (!SingletonJobManager.isInitialized())
            throw new IllegalStateException("SingletonJobManager must be initialized before " + LOG_TAG);
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = database;

        SingletonJobManager.getInstance().stop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mInstance.initDaos();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "initialize", e);
                    // FIXME: 27.5.16 find way to report this error to application to solve this problem
                } finally {
                    SingletonJobManager.getInstance().start();
                }
            }
        }).start();
    }

    public static boolean isInitialized() {
        return mInstance != null;
    }

    public static DatabaseBase getInstance() {
        return mInstance;
    }

    public static <DT> DatabaseDaoGetter<DT> getGetterFor(Class<DT> dataClass) {
        return new SingletonDatabaseDaoGetter<>(dataClass);
    }
}

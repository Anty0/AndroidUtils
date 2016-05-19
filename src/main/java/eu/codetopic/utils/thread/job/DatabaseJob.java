package eu.codetopic.utils.thread.job;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.RetryConstraint;

import java.util.Arrays;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.data.database.DependencyDao;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public class DatabaseJob<T, ID> extends LoadingJob {

    private static final String LOG_TAG = "DatabaseJob";
    private static final String JOB_DATABASE_GROUP_NAME_ADD = ".DATABASE_GROUP";
    private final DatabaseDaoGetter<T> daoGetter;
    private final DatabaseWork<T, ID> job;

    public DatabaseJob(@Nullable LoadingViewHolder loadingViewHolder,
                       DatabaseDaoGetter<T> daoGetter, DatabaseWork<T, ID> job) {
        super(new Params(Constants.JOB_PRIORITY_DATABASE)
                .groupBy(generateDatabaseJobGroupNameFor(daoGetter
                        .getDaoObjectClass())), loadingViewHolder);
        this.daoGetter = daoGetter;
        this.job = job;
    }

    public DatabaseJob(DatabaseDaoGetter<T> daoGetter, DatabaseWork<T, ID> job) {
        this(null, daoGetter, job);
    }

    @SafeVarargs
    public DatabaseJob(@Nullable LoadingViewHolder loadingViewHolder,
                       DatabaseDaoGetter<T> daoGetter, Modification modification, T... toModify) {
        this(loadingViewHolder, daoGetter, modification.<T, ID>generateWork(toModify));
    }

    @SafeVarargs
    public DatabaseJob(DatabaseDaoGetter<T> daoGetter, Modification modification, T... toModify) {
        this(null, daoGetter, modification, toModify);
    }

    @SafeVarargs
    public static <T, ID> void saveData(DatabaseDaoGetter<T> daoGetter, T... toModify) {
        DatabaseJob.<T, ID>start(null, daoGetter, Modification.CREATE_OR_UPDATE, toModify);
    }

    @SafeVarargs
    public static <T, ID> void saveData(@Nullable LoadingViewHolder loadingViewHolder,
                                        DatabaseDaoGetter<T> daoGetter, T... toModify) {
        DatabaseJob.<T, ID>start(loadingViewHolder, daoGetter, Modification.CREATE_OR_UPDATE, toModify);
    }

    @SafeVarargs
    public static <T, ID> void deleteData(DatabaseDaoGetter<T> daoGetter, T... toModify) {
        DatabaseJob.<T, ID>start(null, daoGetter, Modification.DELETE, toModify);
    }

    @SafeVarargs
    public static <T, ID> void deleteData(@Nullable LoadingViewHolder loadingViewHolder,
                                          DatabaseDaoGetter<T> daoGetter, T... toModify) {
        DatabaseJob.<T, ID>start(loadingViewHolder, daoGetter, Modification.DELETE, toModify);
    }

    @SafeVarargs
    public static <T, ID> long start(DatabaseDaoGetter<T> daoGetter, Modification modification, T... toModify) {
        return DatabaseJob.<T, ID>start(null, daoGetter, modification, toModify);
    }

    @SafeVarargs
    public static <T, ID> long start(@Nullable LoadingViewHolder loadingViewHolder,
                                     DatabaseDaoGetter<T> daoGetter, Modification modification, T... toModify) {
        return start(loadingViewHolder, daoGetter, modification.<T, ID>generateWork(toModify));
    }

    public static <T, ID> long start(DatabaseDaoGetter<T> daoGetter, DatabaseWork<T, ID> job) {
        return start(null, daoGetter, job);
    }

    public static <T, ID> long start(@Nullable LoadingViewHolder loadingViewHolder,
                                     DatabaseDaoGetter<T> daoGetter, DatabaseWork<T, ID> job) {
        return daoGetter.getJobManager().addJob(new DatabaseJob<>(loadingViewHolder, daoGetter, job));
    }

    public static String generateDatabaseJobGroupNameFor(Class databaseObject) {
        return databaseObject.getName() + JOB_DATABASE_GROUP_NAME_ADD;
    }

    @Override
    public void onStart() throws Throwable {
        //noinspection unchecked
        job.run((Dao<T, ID>) daoGetter.get());
    }

    @Override
    protected int getRetryLimit() {
        return Constants.JOB_REPEAT_COUNT_DATABASE;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        Log.e(LOG_TAG, "shouldReRunOnThrowable", throwable);
        return super.shouldReRunOnThrowable(throwable, runCount, maxRunCount);
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        Toast.makeText(getApplicationContext(), R.string.toast_text_database_exception,
                Toast.LENGTH_LONG).show();
    }

    public enum Modification {
        CREATE, CREATE_OR_UPDATE, DELETE, DELETE_FROM_TEMP;

        @SafeVarargs
        final <T, ID> DatabaseWork<T, ID> generateWork(final T... toModify) {
            switch (this) {
                case CREATE:
                    return new DatabaseWork<T, ID>() {
                        @Override
                        public void run(Dao<T, ID> dao) throws Throwable {
                            for (T object : toModify) {
                                dao.create(object);
                            }
                        }
                    };
                case CREATE_OR_UPDATE:
                    return new DatabaseWork<T, ID>() {
                        @Override
                        public void run(Dao<T, ID> dao) throws Throwable {
                            for (T object : toModify) {
                                dao.createOrUpdate(object);
                            }
                        }
                    };
                case DELETE:
                    return new DatabaseWork<T, ID>() {
                        @Override
                        public void run(Dao<T, ID> dao) throws Throwable {
                            dao.delete(Arrays.asList(toModify));
                        }
                    };
                case DELETE_FROM_TEMP:
                    return new DatabaseWork<T, ID>() {
                        @Override
                        public void run(Dao<T, ID> dao) throws Throwable {
                            //noinspection unchecked
                            ((DependencyDao) dao).deleteFromTemp(Arrays.asList(toModify));
                        }
                    };
                default:
                    throw new UnknownError();
            }
        }
    }

    public interface DatabaseWork<T, ID> {

        @WorkerThread
        void run(Dao<T, ID> dao) throws Throwable;
    }
}

package eu.codetopic.utils.thread.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Params;
import com.j256.ormlite.dao.Dao;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.data.database.DependencyDao;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;

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

    public static <T, ID> DatabaseJobCreator<T, ID> work(DatabaseDaoGetter<T> daoGetter) {
        return new DatabaseJobCreator<>(daoGetter);
    }

    public static String generateDatabaseJobGroupNameFor(Class<?> databaseObject) {
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
    protected void onCancel(@CancelReason int cancelReason) {
        super.onCancel(cancelReason);
        Toast.makeText(getApplicationContext(), R.string.toast_text_database_exception,
                Toast.LENGTH_LONG).show();
    }

    public enum Modification {
        CREATE, CREATE_OR_UPDATE, DELETE, DELETE_FROM_TEMP;

        @SafeVarargs
        public final <T, ID> DatabaseWork<T, ID> generateWork(final T... toModify) {
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

    public static final class DatabaseJobCreator<T, ID> {

        private final DatabaseDaoGetter<T> daoGetter;
        private LoadingViewHolder loadingHolder = null;

        private DatabaseJobCreator(DatabaseDaoGetter<T> daoGetter) {
            this.daoGetter = daoGetter;
        }

        public DatabaseJobCreator<T, ID> withLoading(LoadingViewHolder loadingHolder) {
            this.loadingHolder = loadingHolder;
            return this;
        }

        @SafeVarargs
        public final String startSave(T... toSave) {
            return start(Modification.CREATE_OR_UPDATE, toSave);
        }

        @SafeVarargs
        public final String startDelete(T... toDelete) {
            return start(Modification.DELETE, toDelete);
        }

        @SafeVarargs
        public final String start(Modification modification, T... toModify) {
            return start(modification.<T, ID>generateWork(toModify));
        }

        public String start(DatabaseWork<T, ID> work) {
            DatabaseJob<T, ID> job = new DatabaseJob<>(loadingHolder, daoGetter, work);
            daoGetter.getJobManager().addJobInBackground(job);
            return job.getId();
        }
    }

    public static abstract class DatabaseQueryForEqWork<W, T, ID> extends DatabaseCallbackWork<W, List<T>, T, ID> {

        private final String fieldName;
        private final Object value;

        public DatabaseQueryForEqWork(@NonNull Context context, W weakData, String fieldName, Object value) {
            super(context, weakData);
            this.fieldName = fieldName;
            this.value = value;
        }

        @Override
        public List<T> work(@NonNull Context appContext, Dao<T, ID> dao) throws Throwable {
            return dao.queryForEq(fieldName, value);
        }
    }

    public static abstract class DatabaseQueryForIdWork<W, T, ID> extends DatabaseCallbackWork<W, T, T, ID> {

        private final ID id;

        public DatabaseQueryForIdWork(@NonNull Context context, W weakData, ID id) {
            super(context, weakData);
            this.id = id;
        }

        @Override
        public T work(@NonNull Context appContext, Dao<T, ID> dao) throws Throwable {
            return dao.queryForId(id);
        }
    }

    public static abstract class DatabaseQueryForAllWork<W, T, ID> extends DatabaseCallbackWork<W, List<T>, T, ID> {

        public DatabaseQueryForAllWork(@NonNull Context context, W weakData) {
            super(context, weakData);
        }

        @Override
        public List<T> work(@NonNull Context appContext, Dao<T, ID> dao) throws Throwable {
            return dao.queryForAll();
        }
    }

    public static abstract class DatabaseCallbackWork<W, D, T, ID> implements DatabaseWork<T, ID> {

        private final Context appContext;
        private final WeakReference<Context> contextRef;
        private final WeakReference<W> weakDataRef;

        public DatabaseCallbackWork(@NonNull Context context, W weakData) {
            this.appContext = context.getApplicationContext();
            this.contextRef = new WeakReference<>(context);
            this.weakDataRef = new WeakReference<>(weakData);
        }

        @Override
        public final void run(Dao<T, ID> dao) throws Throwable {
            D result = null;
            Throwable throwable = null;
            try {
                result = work(appContext, dao);
            } catch (final Throwable t) {
                Log.d(LOG_TAG, "start", t);
                throwable = t;
            } finally {
                final D finalResult = result;
                final Throwable finalThrowable = throwable;
                JobUtils.runOnContextThread(contextRef.get(), new Runnable() {
                    @Override
                    public void run() {
                        finish(appContext, contextRef.get(), weakDataRef.get(),
                                finalResult, finalThrowable);
                    }
                });
            }
        }

        @WorkerThread
        public abstract D work(@NonNull Context appContext, Dao<T, ID> dao) throws Throwable;

        @UiThread
        public abstract void finish(@NonNull Context appContext, @Nullable Context context,
                                    @Nullable W weakData, D result, Throwable throwable);

    }
}

package eu.codetopic.utils.thread.job.database;

import android.support.annotation.Nullable;
import android.widget.Toast;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Params;
import com.j256.ormlite.dao.Dao;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.LoadingJob;

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

    @SafeVarargs
    public static <T, ID> String saveData(DatabaseDaoGetter<T> daoGetter, T... toSave) {
        return start(daoGetter, Modification.CREATE_OR_UPDATE.<T, ID>generateWork(toSave));
    }

    @SafeVarargs
    public static <T, ID> String deleteData(DatabaseDaoGetter<T> daoGetter, T... toDelete) {
        return start(daoGetter, Modification.DELETE.<T, ID>generateWork(toDelete));
    }

    public static <T, ID> String start(DatabaseDaoGetter<T> daoGetter, DatabaseWork<T, ID> work) {
        return start(daoGetter, null, work);
    }

    public static <T, ID> String start(DatabaseDaoGetter<T> daoGetter,
                                       @Nullable LoadingViewHolder loadingHolder,
                                       DatabaseWork<T, ID> work) {

        DatabaseJob<T, ID> job = new DatabaseJob<>(loadingHolder, daoGetter, work);
        daoGetter.getJobManager().addJobInBackground(job);
        return job.getId();
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
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.toast_text_database_exception,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

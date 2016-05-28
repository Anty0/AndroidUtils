package eu.codetopic.utils.thread.job.database;

import android.support.annotation.Nullable;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public class DbJob {

    private static final String LOG_TAG = "DbJob";

    public static <T, ID> String all(DatabaseDaoGetter<T> daoGetter, Callback<List<T>> callback) {
        return DbJob.<T, ID>all(daoGetter, null, callback);
    }

    public static <T, ID> String all(DatabaseDaoGetter<T> daoGetter,
                                     @Nullable LoadingViewHolder loadingHolder,
                                     final Callback<List<T>> callback) {
        return start(daoGetter, loadingHolder, new DatabaseWork<T, ID>() {
            @Override
            public void run(Dao<T, ID> dao) throws Throwable {

            }
        });
    }

    @SafeVarargs
    public static <T, ID> String save(DatabaseDaoGetter<T> daoGetter, T... toSave) {
        return start(daoGetter, Modification.CREATE_OR_UPDATE.<T, ID>generateWork(toSave));
    }

    @SafeVarargs
    public static <T, ID> String delete(DatabaseDaoGetter<T> daoGetter, T... toDelete) {
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

    public interface Callback<T> {

        void onResult(T result);
    }

}

package eu.codetopic.utils.thread.job.database;

import android.os.Handler;
import android.os.Looper;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public final class DbJob<T> {

    private static final String LOG_TAG = "DbJob";

    private final DatabaseDaoGetter<T> daoGetter;
    private LoadingViewHolder loadingHolder = null;

    private DbJob(DatabaseDaoGetter<T> daoGetter) {
        this.daoGetter = daoGetter;
    }

    public static <T> DbJob<T> work(DatabaseDaoGetter<T> daoGetter) {
        return new DbJob<>(daoGetter);
    }

    public DbJob<T> withLoading(LoadingViewHolder loadingHolder) {
        this.loadingHolder = loadingHolder;
        return this;
    }

    public <ID> String forEq(final String fieldName, final Object value,
                             final Callback<List<T>> callback) {
        return startCallback(new CallbackWork<List<T>, T, ID>() {
            @Override
            public List<T> run(Dao<T, ID> dao) throws Throwable {
                return dao.queryForEq(fieldName, value);
            }
        }, callback);
    }

    public <ID> String forId(final ID id, final Callback<T> callback) {
        return startCallback(new CallbackWork<T, T, ID>() {
            @Override
            public T run(Dao<T, ID> dao) throws Throwable {
                return dao.queryForId(id);
            }
        }, callback);
    }

    public <ID> String forAll(final Callback<List<T>> callback) {
        return startCallback(new CallbackWork<List<T>, T, ID>() {
            @Override
            public List<T> run(Dao<T, ID> dao) throws Throwable {
                return dao.queryForAll();
            }
        }, callback);
    }

    public <D, ID> String startCallback(final CallbackWork<D, T, ID> work, final Callback<D> callback) {
        final Handler handler = new Handler(Looper.myLooper());
        return start(new DatabaseWork<T, ID>() {
            @Override
            public void run(Dao<T, ID> dao) throws Throwable {
                try {
                    final D result = work.run(dao);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(result);
                        }
                    });
                } catch (final Throwable t) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(t);
                        }
                    });
                    throw t;
                }
            }
        });
    }

    @SafeVarargs
    public final <ID> String save(T... toSave) {
        return start(Modification.CREATE_OR_UPDATE.<T, ID>generateWork(toSave));
    }

    @SafeVarargs
    public final <ID> String delete(T... toDelete) {
        return start(Modification.DELETE.<T, ID>generateWork(toDelete));
    }

    public <ID> String start(DatabaseWork<T, ID> work) {
        DatabaseJob<T, ID> job = new DatabaseJob<>(loadingHolder, daoGetter, work);
        daoGetter.getJobManager().addJobInBackground(job);
        return job.getId();
    }

    public interface CallbackWork<D, T, ID> {

        D run(Dao<T, ID> dao) throws Throwable;
    }

    public static abstract class Callback<T> {

        public abstract void onResult(T result);

        public void onException(Throwable t) {
        }
    }

}

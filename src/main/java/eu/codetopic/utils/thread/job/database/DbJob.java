/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.thread.job.database;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;

import java.util.List;

import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

public final class DbJob<T, ID> {

    private static final String LOG_TAG = "DbJob";

    private final DatabaseDaoGetter<T, ID> daoGetter;
    private LoadingVH loadingHolder = null;

    private DbJob(DatabaseDaoGetter<T, ID> daoGetter) {
        this.daoGetter = daoGetter;
    }

    public static <T, ID> DbJob<T, ID> work(DatabaseDaoGetter<T, ID> daoGetter) {
        return new DbJob<>(daoGetter);
    }

    public DbJob<T, ID> withLoading(LoadingVH loadingHolder) {
        this.loadingHolder = loadingHolder;
        return this;
    }

    public String forEq(final String fieldName, final Object value,
                        final Callback<List<T>> callback) {
        return startCallback(new CallbackWork<List<T>, T, ID>() {
            @WorkerThread
            @Override
            public List<T> run(Dao<T, ID> dao) throws Throwable {
                return dao.query(dao.queryBuilder().where()
                        .eq(fieldName, new SelectArg(value)).prepare());
            }
        }, callback);
    }

    public String forId(final ID id, final Callback<T> callback) {
        return startCallback(new CallbackWork<T, T, ID>() {
            @WorkerThread
            @Override
            public T run(Dao<T, ID> dao) throws Throwable {
                return dao.queryForId(id);
            }
        }, callback);
    }

    public String forAll(final Callback<List<T>> callback) {
        return startCallback(new CallbackWork<List<T>, T, ID>() {
            @WorkerThread
            @Override
            public List<T> run(Dao<T, ID> dao) throws Throwable {
                return dao.queryForAll();
            }
        }, callback);
    }

    public String startCallback(final DatabaseWork<T, ID> work, final Callback<Void> callback) {
        return startCallback(new CallbackWork<Void, T, ID>() {
            @Override
            public Void run(Dao<T, ID> dao) throws Throwable {
                work.run(dao);
                return null;
            }
        }, callback);
    }

    public <D> String startCallback(final CallbackWork<D, T, ID> work, final Callback<D> callback) {
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
    public final String save(T... toSave) {
        return start(Modification.CREATE_OR_UPDATE.<T, ID>generateWork(toSave));
    }

    @SafeVarargs
    public final String delete(T... toDelete) {
        return start(Modification.DELETE.<T, ID>generateWork(toDelete));
    }

    public String start(DatabaseWork<T, ID> work) {
        DatabaseJob<T, ID> job = new DatabaseJob<>(loadingHolder, daoGetter, work);
        daoGetter.getJobManager().addJobInBackground(job);
        return job.getId();
    }

    public interface CallbackWork<D, T, ID> {

        @WorkerThread
        D run(Dao<T, ID> dao) throws Throwable;
    }

    public static abstract class Callback<T> {

        public abstract void onResult(T result);

        public void onException(Throwable t) {
        }
    }

}

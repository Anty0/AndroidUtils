package eu.codetopic.utils.thread.job.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;

import java.util.List;

public abstract class DatabaseQueryForAllWork<W, T, ID>
        extends DatabaseCallbackWork<W, List<T>, T, ID> {

    public DatabaseQueryForAllWork(@NonNull Context context, W weakData) {
        super(context, weakData);
    }

    @Override
    public List<T> work(Dao<T, ID> dao) throws Throwable {
        return dao.queryForAll();
    }
}
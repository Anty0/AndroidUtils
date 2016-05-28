package eu.codetopic.utils.thread.job.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;

public abstract class DatabaseQueryForIdWork<W, T, ID> extends DatabaseCallbackWork<W, T, T, ID> {

    private final ID id;

    public DatabaseQueryForIdWork(@NonNull Context context, W weakData, ID id) {
        super(context, weakData);
        this.id = id;
    }

    @Override
    public T work(Dao<T, ID> dao) throws Throwable {
        return dao.queryForId(id);
    }
}

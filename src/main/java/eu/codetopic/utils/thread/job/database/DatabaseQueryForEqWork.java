package eu.codetopic.utils.thread.job.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.j256.ormlite.dao.Dao;

import java.util.List;

public abstract class DatabaseQueryForEqWork<W, T, ID> extends DatabaseCallbackWork<W, List<T>, T, ID> {

    private final String fieldName;
    private final Object value;

    public DatabaseQueryForEqWork(@NonNull Context context, W weakData, String fieldName, Object value) {
        super(context, weakData);
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public List<T> work(Dao<T, ID> dao) throws Throwable {
        return dao.queryForEq(fieldName, value);
    }
}
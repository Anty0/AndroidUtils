package eu.codetopic.utils.data.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedUpdate;

import java.sql.SQLException;
import java.util.Collection;

import eu.codetopic.utils.log.Log;

public class DatabaseObjectDao<T> extends DaoWrapper<T, Long> {

    private static final String LOG_TAG = "DatabaseObjectDao";

    private final DatabaseObjectChangeDetector<T> mChangeDetector;

    DatabaseObjectDao(Dao<T, Long> baseDao, DatabaseObjectChangeDetector<T> dataChangedDetector) {
        super(baseDao);
        Log.d(LOG_TAG, "<init>");
        mChangeDetector = dataChangedDetector;
    }

    public DatabaseObjectChangeDetector<T> getChangeDetector() {
        return mChangeDetector;
    }

    @Override
    public int create(T data) throws SQLException {
        int toReturn = super.create(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public T createIfNotExists(T data) throws SQLException {
        T toReturn = super.createIfNotExists(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(T data) throws SQLException {
        CreateOrUpdateStatus toReturn = super.createOrUpdate(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int update(T data) throws SQLException {
        int toReturn = super.update(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int updateId(T data, Long newId) throws SQLException {
        int toReturn = super.updateId(data, newId);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int update(PreparedUpdate<T> preparedUpdate) throws SQLException {
        int toReturn = super.update(preparedUpdate);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int refresh(T data) throws SQLException {
        int toReturn = super.refresh(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int delete(T data) throws SQLException {
        int toReturn = super.delete(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int delete(Collection<T> data) throws SQLException {
        int toReturn = super.delete(data);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        int toReturn = super.delete(preparedDelete);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int deleteById(Long id) throws SQLException {
        int toReturn = super.deleteById(id);
        mChangeDetector.onChange();
        return toReturn;
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws SQLException {
        int toReturn = super.deleteIds(ids);
        mChangeDetector.onChange();
        return toReturn;
    }
}

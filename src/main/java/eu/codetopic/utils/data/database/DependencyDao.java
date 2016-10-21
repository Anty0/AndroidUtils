package eu.codetopic.utils.data.database;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DatabaseResultsMapper;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RawRowObjectMapper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.DatabaseResults;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import eu.codetopic.java.utils.log.Log;

public class DependencyDao<T extends DependencyDatabaseObject> extends DaoWrapper<T, Long> {// TODO: 17.8.16 rework

    private static final String LOG_TAG = "DependencyDao";

    private final DependencyObjectManager<T> mDependencyObjectManager;

    DependencyDao(Dao<T, Long> baseDao, DependencyObjectManager<T> dependencyObjectManager) {
        super(baseDao);
        Log.d(LOG_TAG, "<init> for " + getDataClass());
        mDependencyObjectManager = dependencyObjectManager;
    }

    void removeUnneededData() throws SQLException {
        List<T> before = Log.isInDebugMode() ? queryForAllWithTemp() : null;
        mDependencyObjectManager.removeUnneededData(this);
        if (Log.isInDebugMode())
            Log.d(LOG_TAG, "removeUnneededData for " + getDataClass().getName()
                    + "\nbefore: " + before + "\nafter: " + queryForAllWithTemp());
    }

    private List<T> filterDeleted(List<T> toFilter) {
        ArrayList<T> toReturn = new ArrayList<>();
        for (T object : toFilter) {
            if (!object.isDeleted())
                toReturn.add(object);
        }
        return toReturn;
    }

    private List<T> filterDeleted(CloseableIterator<T> toFilter) throws SQLException {
        ArrayList<T> toReturn = new ArrayList<>();
        while (toFilter.hasNext()) {
            T object = toFilter.nextThrow();
            if (!object.isDeleted())
                toReturn.add(object);
        }
        try {
            toFilter.close();
        } catch (IOException e) {
            throw new SQLException(e);
        }
        return toReturn;
    }

    @Override
    public int create(Collection<T> datas) throws SQLException {
        int toReturn = super.create(datas);
        mDependencyObjectManager.onUpdateObjects(this, datas);
        return toReturn;
    }

    @Override
    public int create(T data) throws SQLException {
        int toReturn = super.create(data);
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public T createIfNotExists(T data) throws SQLException {
        T toReturn = super.createIfNotExists(data);
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(T data) throws SQLException {
        CreateOrUpdateStatus toReturn = super.createOrUpdate(data);
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public int update(T data) throws SQLException {
        int toReturn = super.update(data);
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public int updateId(T data, Long newId) throws SQLException {
        int toReturn = super.updateId(data, newId);
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public int update(PreparedUpdate<T> preparedUpdate) throws SQLException {
        int toReturn = super.update(preparedUpdate);
        mDependencyObjectManager.onUpdateAll(this);
        return toReturn;
    }

    @Override
    public int refresh(T data) throws SQLException {
        int toReturn = super.refresh(data);
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public int delete(T data) throws SQLException {
        data.delete();
        int toReturn = super.createOrUpdate(data).getNumLinesChanged();
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    @Override
    public int delete(Collection<T> data) throws SQLException {
        int toReturn = 0;
        for (T object : data) {
            object.delete();
            toReturn += super.createOrUpdate(object).getNumLinesChanged();
        }
        mDependencyObjectManager.onUpdateObjects(this, data);
        return toReturn;
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public int deleteFromTemp(T data) throws SQLException {
        return super.delete(data);
    }

    public int deleteFromTemp(Collection<T> data) throws SQLException {
        return super.delete(data);
    }

    @Override
    public int deleteById(Long id) throws SQLException {
        T data = queryForId(id);
        if (data == null) return 0;
        data.delete();
        int toReturn = super.createOrUpdate(data).getNumLinesChanged();
        mDependencyObjectManager.onUpdateObject(this, data);
        return toReturn;
    }

    public int deleteByIdFromTemp(Long id) throws SQLException {
        return super.deleteById(id);
    }

    @Override
    public int deleteIds(Collection<Long> ids) throws SQLException {
        List<T> data = new ArrayList<>();
        int toReturn = 0;
        for (Long id : ids) {
            T obj = queryForId(id);
            if (obj == null) continue;
            data.add(obj);
            obj.delete();
            toReturn += super.createOrUpdate(obj).getNumLinesChanged();
        }
        mDependencyObjectManager.onUpdateObjects(this, data);
        return toReturn;
    }

    public int deleteIdsFromTemp(Collection<Long> ids) throws SQLException {
        return super.deleteIds(ids);
    }

    public CreateOrUpdateStatus saveWithOutUpdate(T data) throws SQLException {
        return super.createOrUpdate(data);
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        return filterDeleted(super.iterator());
    }

    public List<T> queryForAllWithTemp() throws SQLException {
        return super.queryForAll();
    }

    @Override
    public T queryForId(Long id) throws SQLException {
        T result = super.queryForId(id);
        return result == null || result.isDeleted() ? null : result;
    }

    public T queryForIdWithTemp(Long id) throws SQLException {
        return super.queryForId(id);
    }

    @Override
    public T queryForFirst(PreparedQuery<T> preparedQuery) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public T queryForFirstWithTemp(PreparedQuery<T> preparedQuery) throws SQLException {
        return super.queryForFirst(preparedQuery);
    }

    @Override
    public List<T> queryForEq(String fieldName, Object value) throws SQLException {
        return filterDeleted(super.queryForEq(fieldName, value));
    }

    public List<T> queryForEqWithTemp(String fieldName, Object value) throws SQLException {
        return super.queryForEq(fieldName, value);
    }

    @Override
    public List<T> queryForMatching(T matchObj) throws SQLException {
        return filterDeleted(super.queryForMatching(matchObj));
    }

    public List<T> queryForMatchingWithTemp(T matchObj) throws SQLException {
        return super.queryForMatching(matchObj);
    }

    @Override
    public List<T> queryForMatchingArgs(T matchObj) throws SQLException {
        return filterDeleted(super.queryForMatchingArgs(matchObj));
    }

    public List<T> queryForMatchingArgsWithTemp(T matchObj) throws SQLException {
        return super.queryForMatchingArgs(matchObj);
    }

    @Override
    public List<T> queryForFieldValues(Map<String, Object> fieldValues) throws SQLException {
        return filterDeleted(super.queryForFieldValues(fieldValues));
    }

    public List<T> queryForFieldValuesWithTemp(Map<String, Object> fieldValues) throws SQLException {
        return super.queryForFieldValues(fieldValues);
    }

    @Override
    public List<T> queryForFieldValuesArgs(Map<String, Object> fieldValues) throws SQLException {
        return filterDeleted(super.queryForFieldValuesArgs(fieldValues));
    }

    public List<T> queryForFieldValuesArgsWithTemp(Map<String, Object> fieldValues) throws SQLException {
        return super.queryForFieldValuesArgs(fieldValues);
    }

    @Override
    public T queryForSameId(T data) throws SQLException {
        T obj = super.queryForSameId(data);
        return obj != null && !obj.isDeleted() ? obj : null;
    }

    @Override
    public QueryBuilder<T, Long> queryBuilder() {
        return super.queryBuilder();
    }

    @Override
    public UpdateBuilder<T, Long> updateBuilder() {
        return super.updateBuilder();
    }

    @Override
    public DeleteBuilder<T, Long> deleteBuilder() {
        return super.deleteBuilder();
    }

    public T queryForSameIdWithTemp(T data) throws SQLException {
        return super.queryForSameId(data);
    }

    @Override
    public List<T> query(PreparedQuery<T> preparedQuery) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public List<T> queryWithTemp(PreparedQuery<T> preparedQuery) throws SQLException {
        return super.query(preparedQuery);
    }

    @Override
    public CloseableIterator<T> iterator() {
        return new TempFilteringCloseableIterator<>(super.iterator());
    }

    public CloseableIterator<T> iteratorWithTemp() {
        return super.iterator();
    }

    @Override
    public CloseableIterator<T> closeableIterator() {
        return new TempFilteringCloseableIterator<>(super.closeableIterator());
    }

    @Override
    public CloseableIterator<T> iterator(int resultFlags) {
        return new TempFilteringCloseableIterator<>(super.iterator(resultFlags));
    }

    public CloseableIterator<T> iteratorWithTemp(int resultFlags) {
        return super.iterator(resultFlags);
    }

    @Override
    public CloseableWrappedIterable<T> getWrappedIterable() {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public CloseableWrappedIterable<T> getWrappedIterable(PreparedQuery<T> preparedQuery) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void closeLastIterator() throws IOException {
        super.closeLastIterator();
    }

    @Override
    public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery) throws SQLException {
        return new TempFilteringCloseableIterator<>(super.iterator(preparedQuery));
    }

    public CloseableIterator<T> iteratorWithTemp(PreparedQuery<T> preparedQuery) throws SQLException {
        return super.iterator(preparedQuery);
    }

    @Override
    public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery, int resultFlags) throws SQLException {
        return new TempFilteringCloseableIterator<>(super.iterator(preparedQuery, resultFlags));
    }

    public CloseableIterator<T> iteratorWithTemp(PreparedQuery<T> preparedQuery, int resultFlags) throws SQLException {
        return super.iterator(preparedQuery, resultFlags);
    }

    @Override
    public GenericRawResults<String[]> queryRaw(String query, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public GenericRawResults<String[]> queryRawWithTemp(String query, String... arguments) throws SQLException {
        return super.queryRaw(query, arguments);
    }

    @Override
    public long queryRawValue(String query, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public long queryRawValueWithTemp(String query, String... arguments) throws SQLException {
        return super.queryRawValue(query, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, RawRowMapper<UO> mapper, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public <UO> GenericRawResults<UO> queryRawWithTemp(String query, RawRowMapper<UO> mapper, String... arguments) throws SQLException {
        return super.queryRaw(query, mapper, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, DataType[] columnTypes,
                                               RawRowObjectMapper<UO> mapper, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public <UO> GenericRawResults<UO> queryRawWithTemp(String query, DataType[] columnTypes,
                                                       RawRowObjectMapper<UO> mapper, String... arguments) throws SQLException {
        return super.queryRaw(query, columnTypes, mapper, arguments);
    }

    @Override
    public GenericRawResults<Object[]> queryRaw(String query, DataType[] columnTypes, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public GenericRawResults<Object[]> queryRawWithTemp(String query, DataType[] columnTypes, String... arguments) throws SQLException {
        return super.queryRaw(query, columnTypes, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, DatabaseResultsMapper<UO> mapper, String... arguments) throws SQLException {
        throw new UnsupportedOperationException("Unsupported");
    }

    public <UO> GenericRawResults<UO> queryRawWithTemp(String query, DatabaseResultsMapper<UO> mapper, String... arguments) throws SQLException {
        return super.queryRaw(query, mapper, arguments);
    }

    @Override
    public int executeRaw(String statement, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public int executeRawNoArgs(String statement) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public int updateRaw(String statement, String... arguments) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public <CT> CT callBatchTasks(Callable<CT> callable) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public long countOf() throws SQLException {
        int count = 0;
        CloseableIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.nextThrow();
            count++;
        }
        try {
            iterator.close();
        } catch (IOException e) {
            throw new SQLException(e);
        }
        return count;
    }

    public long countOfWithTemp() throws SQLException {
        return super.countOf();
    }

    @Override
    public long countOf(PreparedQuery<T> preparedQuery) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public long countOfWithTemp(PreparedQuery<T> preparedQuery) throws SQLException {
        return super.countOf(preparedQuery);
    }

    @Override
    public boolean idExists(Long id) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public boolean idExistsWithTemp(Long id) throws SQLException {
        return super.idExists(id);
    }

    @WorkerThread
    private static class TempFilteringCloseableIterator<T extends DependencyDatabaseObject>
            implements CloseableIterator<T> {

        private final CloseableIterator<T> parent;
        private T next = null;

        TempFilteringCloseableIterator(CloseableIterator<T> parent) {
            this.parent = parent;
            prepareNext();
        }

        private void prepareNext() {
            next = null;
            while (parent.hasNext() && (next == null || next.isDeleted()))
                next = parent.next();
            if (next != null && next.isDeleted()) next = null;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            T next = this.next;
            prepareNext();
            return next;
        }

        @Override
        public void remove() {
            parent.remove();
        }

        @Override
        public void close() throws IOException {
            parent.close();
        }

        @Override
        public void closeQuietly() {
            parent.closeQuietly();
        }

        @Override
        public DatabaseResults getRawResults() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public void moveToNext() {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public T first() throws SQLException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public T previous() throws SQLException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public T current() throws SQLException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public T nextThrow() throws SQLException {
            throw new UnsupportedOperationException("Unsupported");
        }

        @Override
        public T moveRelative(int p1) throws SQLException {
            throw new UnsupportedOperationException("Unsupported");
        }
    }
}

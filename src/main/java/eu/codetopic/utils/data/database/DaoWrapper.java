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

package eu.codetopic.utils.data.database;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DatabaseResultsMapper;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RawRowObjectMapper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.ObjectFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@WorkerThread
public class DaoWrapper<T, ID> implements Dao<T, ID> {

    private final Dao<T, ID> mBase;

    public DaoWrapper(Dao<T, ID> baseDao) {
        mBase = baseDao;
    }

    @Override
    public int create(T data) throws SQLException {
        return mBase.create(data);
    }

    @Override
    public int create(Collection<T> datas) throws SQLException {
        return mBase.create(datas);
    }

    @Override
    public T createIfNotExists(T data) throws SQLException {
        return mBase.createIfNotExists(data);
    }

    @Override
    public CreateOrUpdateStatus createOrUpdate(T data) throws SQLException {
        return mBase.createOrUpdate(data);
    }

    @Override
    public int update(T data) throws SQLException {
        return mBase.update(data);
    }

    @Override
    public int updateId(T data, ID newId) throws SQLException {
        return mBase.updateId(data, newId);
    }

    @Override
    public int update(PreparedUpdate<T> preparedUpdate) throws SQLException {
        return mBase.update(preparedUpdate);
    }

    @Override
    public int refresh(T data) throws SQLException {
        return mBase.refresh(data);
    }

    @Override
    public int delete(T data) throws SQLException {
        return mBase.delete(data);
    }

    @Override
    public int delete(Collection<T> data) throws SQLException {
        return mBase.delete(data);
    }

    @Override
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {
        return mBase.delete(preparedDelete);
    }

    @Override
    public int deleteById(ID id) throws SQLException {
        return mBase.deleteById(id);
    }

    @Override
    public int deleteIds(Collection<ID> ids) throws SQLException {
        return mBase.deleteIds(ids);
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        return mBase.queryForAll();
    }

    @Override
    public T queryForId(ID id) throws SQLException {
        return mBase.queryForId(id);
    }

    @Override
    public T queryForFirst(PreparedQuery<T> preparedQuery) throws SQLException {
        return mBase.queryForFirst(preparedQuery);
    }

    @Override
    public List<T> queryForEq(String fieldName, Object value) throws SQLException {
        return mBase.queryForEq(fieldName, value);
    }

    @Override
    public List<T> queryForMatching(T matchObj) throws SQLException {
        return mBase.queryForMatching(matchObj);
    }

    @Override
    public List<T> queryForMatchingArgs(T matchObj) throws SQLException {
        return mBase.queryForMatchingArgs(matchObj);
    }

    @Override
    public List<T> queryForFieldValues(Map<String, Object> fieldValues) throws SQLException {
        return mBase.queryForFieldValues(fieldValues);
    }

    @Override
    public List<T> queryForFieldValuesArgs(Map<String, Object> fieldValues) throws SQLException {
        return mBase.queryForFieldValuesArgs(fieldValues);
    }

    @Override
    public T queryForSameId(T data) throws SQLException {
        return mBase.queryForSameId(data);
    }

    @Override
    public QueryBuilder<T, ID> queryBuilder() {
        return mBase.queryBuilder();
    }

    @Override
    public UpdateBuilder<T, ID> updateBuilder() {
        return mBase.updateBuilder();
    }

    @Override
    public DeleteBuilder<T, ID> deleteBuilder() {
        return mBase.deleteBuilder();
    }

    @Override
    public List<T> query(PreparedQuery<T> preparedQuery) throws SQLException {
        return mBase.query(preparedQuery);
    }

    @Override
    public CloseableIterator<T> iterator() {
        return mBase.iterator();
    }

    @Override
    public CloseableIterator<T> closeableIterator() {
        return mBase.closeableIterator();
    }

    @Override
    public CloseableIterator<T> iterator(int resultFlags) {
        return mBase.iterator(resultFlags);
    }

    @Override
    public CloseableWrappedIterable<T> getWrappedIterable() {
        return mBase.getWrappedIterable();
    }

    @Override
    public CloseableWrappedIterable<T> getWrappedIterable(PreparedQuery<T> preparedQuery) {
        return mBase.getWrappedIterable(preparedQuery);
    }

    @Override
    public void closeLastIterator() throws IOException {
        mBase.closeLastIterator();
    }

    @Override
    public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery) throws SQLException {
        return mBase.iterator(preparedQuery);
    }

    @Override
    public CloseableIterator<T> iterator(PreparedQuery<T> preparedQuery, int resultFlags) throws SQLException {
        return mBase.iterator(preparedQuery, resultFlags);
    }

    @Override
    public GenericRawResults<String[]> queryRaw(String query, String... arguments) throws SQLException {
        return mBase.queryRaw(query, arguments);
    }

    @Override
    public long queryRawValue(String query, String... arguments) throws SQLException {
        return mBase.queryRawValue(query, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, RawRowMapper<UO> mapper, String... arguments) throws SQLException {
        return mBase.queryRaw(query, mapper, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, DataType[] columnTypes,
                                               RawRowObjectMapper<UO> mapper, String... arguments) throws SQLException {
        return mBase.queryRaw(query, columnTypes, mapper, arguments);
    }

    @Override
    public GenericRawResults<Object[]> queryRaw(String query, DataType[] columnTypes, String... arguments) throws SQLException {
        return mBase.queryRaw(query, columnTypes, arguments);
    }

    @Override
    public <UO> GenericRawResults<UO> queryRaw(String query, DatabaseResultsMapper<UO> mapper, String... arguments) throws SQLException {
        return mBase.queryRaw(query, mapper, arguments);
    }

    @Override
    public int executeRaw(String statement, String... arguments) throws SQLException {
        return mBase.executeRaw(statement, arguments);
    }

    @Override
    public int executeRawNoArgs(String statement) throws SQLException {
        return mBase.executeRawNoArgs(statement);
    }

    @Override
    public int updateRaw(String statement, String... arguments) throws SQLException {
        return mBase.updateRaw(statement, arguments);
    }

    @Override
    public <CT> CT callBatchTasks(Callable<CT> callable) throws Exception {
        return mBase.callBatchTasks(callable);
    }

    @Override
    public String objectToString(T data) {
        return mBase.objectToString(data);
    }

    @Override
    public boolean objectsEqual(T data1, T data2) throws SQLException {
        return mBase.objectsEqual(data1, data2);
    }

    @Override
    public ID extractId(T data) throws SQLException {
        return mBase.extractId(data);
    }

    @Override
    public Class<T> getDataClass() {
        return mBase.getDataClass();
    }

    @Override
    public FieldType findForeignFieldType(Class<?> clazz) {
        return mBase.findForeignFieldType(clazz);
    }

    @Override
    public boolean isUpdatable() {
        return mBase.isUpdatable();
    }

    @Override
    public boolean isTableExists() throws SQLException {
        return mBase.isTableExists();
    }

    @Override
    public long countOf() throws SQLException {
        return mBase.countOf();
    }

    @Override
    public long countOf(PreparedQuery<T> preparedQuery) throws SQLException {
        return mBase.countOf(preparedQuery);
    }

    @Override
    public void assignEmptyForeignCollection(T parent, String fieldName) throws SQLException {
        mBase.assignEmptyForeignCollection(parent, fieldName);
    }

    @Override
    public <FT> ForeignCollection<FT> getEmptyForeignCollection(String fieldName) throws SQLException {
        return mBase.getEmptyForeignCollection(fieldName);
    }

    @Override
    public void setObjectCache(boolean enabled) throws SQLException {
        mBase.setObjectCache(enabled);
    }

    @Override
    public ObjectCache getObjectCache() {
        return mBase.getObjectCache();
    }

    @Override
    public void setObjectCache(ObjectCache objectCache) throws SQLException {
        mBase.setObjectCache(objectCache);
    }

    @Override
    public void clearObjectCache() {
        mBase.clearObjectCache();
    }

    @Override
    public T mapSelectStarRow(DatabaseResults results) throws SQLException {
        return mBase.mapSelectStarRow(results);
    }

    @Override
    public GenericRowMapper<T> getSelectStarRowMapper() throws SQLException {
        return mBase.getSelectStarRowMapper();
    }

    @Override
    public RawRowMapper<T> getRawRowMapper() {
        return mBase.getRawRowMapper();
    }

    @Override
    public boolean idExists(ID id) throws SQLException {
        return mBase.idExists(id);
    }

    @Override
    public DatabaseConnection startThreadConnection() throws SQLException {
        return mBase.startThreadConnection();
    }

    @Override
    public void endThreadConnection(DatabaseConnection connection) throws SQLException {
        mBase.endThreadConnection(connection);
    }

    @Override
    public void setAutoCommit(DatabaseConnection connection, boolean autoCommit) throws SQLException {
        mBase.setAutoCommit(connection, autoCommit);
    }

    @Override
    public boolean isAutoCommit(DatabaseConnection connection) throws SQLException {
        return mBase.isAutoCommit(connection);
    }

    @Override
    public void commit(DatabaseConnection connection) throws SQLException {
        mBase.commit(connection);
    }

    @Override
    public void rollBack(DatabaseConnection connection) throws SQLException {
        mBase.rollBack(connection);
    }

    @Override
    public ConnectionSource getConnectionSource() {
        return mBase.getConnectionSource();
    }

    @Override
    public void setObjectFactory(ObjectFactory<T> objectFactory) {
        mBase.setObjectFactory(objectFactory);
    }

    @Override
    public void registerObserver(DaoObserver observer) {
        mBase.registerObserver(observer);
    }

    @Override
    public void unregisterObserver(DaoObserver observer) {
        mBase.unregisterObserver(observer);
    }

    @Override
    public String getTableName() {
        return mBase.getTableName();
    }

    @Override
    public void notifyChanges() {
        mBase.notifyChanges();
    }
}

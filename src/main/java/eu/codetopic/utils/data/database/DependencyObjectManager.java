/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.data.database;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.CloseableIterator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DependencyObjectManager<T extends DependencyDatabaseObject> {

    private static final String LOG_TAG = "DependencyObjectManager";

    private final Context mContext;
    private final DatabaseObjectChangeDetector<T> mChangeDetector;
    private DatabaseBase mDatabase = null;

    public DependencyObjectManager(Context context, Class<T> clazz) {
        mContext = context;
        mChangeDetector = new DatabaseObjectChangeDetector<>(context, clazz);
    }

    void bindDatabase(DatabaseBase mDatabase) {
        this.mDatabase = mDatabase;
    }

    protected Class[] getDependencies() {
        return new Class[0];
    }

    protected DependenciesDetector<T> getDependenciesDetector() {
        return new DefaultDependenciesDetector<>();
    }

    @WorkerThread
    public void onUpdateObject(DependencyDao<T> dao, T object) throws SQLException {
        if (mDatabase == null)
            throw new IllegalStateException(LOG_TAG + " is not bind to database");

        if (object instanceof DependencyTextDatabaseObject) {
            ((DependencyTextDatabaseObject) object).updateText(mContext);
            dao.saveWithOutUpdate(object);
        }

        DependenciesDetector<T> dependenciesDetector = getDependenciesDetector();
        for (Class clazz : getDependencies()) {
            if (!DependencyTextDatabaseObject.class.isAssignableFrom(clazz)) continue;
            //noinspection RedundantCast
            DependencyDao foundDao = (DependencyDao) mDatabase.getDao(clazz);
            for (Object o : foundDao.queryForAll()) {
                if (dependenciesDetector.depends(object, (DependencyTextDatabaseObject) o)) {
                    ((DependencyTextDatabaseObject) o).updateText(mContext);
                    //noinspection unchecked
                    foundDao.saveWithOutUpdate((DependencyTextDatabaseObject) o);
                }
            }
        }

        mChangeDetector.onChange();
    }

    @WorkerThread
    public void onUpdateObjects(DependencyDao<T> dao, Collection<T> objects) throws SQLException {
        if (mDatabase == null)
            throw new IllegalStateException(LOG_TAG + " is not bind to database");

        DependenciesDetector<T> dependenciesDetector = getDependenciesDetector();
        Class[] dependencies = getDependencies();
        List<DependencyDao> daos = new ArrayList<>();
        for (Class daoClass : dependencies)
            if (DependencyTextDatabaseObject.class.isAssignableFrom(daoClass))
                daos.add((DependencyDao) mDatabase.getDao(daoClass));

        for (T object : objects) {
            if (object instanceof DependencyTextDatabaseObject) {
                ((DependencyTextDatabaseObject) object).updateText(mContext);
                dao.saveWithOutUpdate(object);
            }

            for (DependencyDao foundDao : daos) {
                for (Object o : foundDao.queryForAll()) {
                    if (dependenciesDetector.depends(object, (DependencyTextDatabaseObject) o)) {
                        ((DependencyTextDatabaseObject) o).updateText(mContext);
                        //noinspection unchecked
                        foundDao.saveWithOutUpdate((DependencyTextDatabaseObject) o);
                    }
                }
            }
        }

        mChangeDetector.onChange();
    }

    @WorkerThread
    public void onUpdateAll(DependencyDao<T> dao) throws SQLException {
        onUpdateObjects(dao, dao.queryForAll());
    }

    @WorkerThread
    public void removeUnneededData(DependencyDao<T> dao) throws SQLException {
        ArrayList<T> toDelete = new ArrayList<>();
        CloseableIterator<T> iterator = dao.iteratorWithTemp();
        while (iterator.hasNext()) {
            T data = iterator.next();
            if (data.isDeleted() && !data.isRequired())
                toDelete.add(data);
        }
        try {
            iterator.close();
        } catch (IOException e) {
            throw new SQLException(e);
        }

        dao.deleteFromTemp(toDelete);
        mChangeDetector.onChange();
    }

    public interface DependenciesDetector<T extends DependencyDatabaseObject> {

        @WorkerThread
        boolean depends(T modified, DependencyTextDatabaseObject toUpdate) throws SQLException;
    }

    public static class DefaultDependenciesDetector<T extends DependencyDatabaseObject>
            implements DependenciesDetector<T> {

        @Override
        public boolean depends(T modified, DependencyTextDatabaseObject toUpdate) {
            return true;
        }
    }

}

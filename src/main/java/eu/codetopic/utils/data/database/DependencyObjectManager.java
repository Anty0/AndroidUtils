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

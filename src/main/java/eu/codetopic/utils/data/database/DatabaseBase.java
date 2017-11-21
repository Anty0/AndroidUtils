/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

import eu.codetopic.java.utils.log.Log;

public abstract class DatabaseBase extends OrmLiteSqliteOpenHelper {

    private static final String LOG_TAG = "DatabaseBase";

    protected final Context mContext;
    private final Class[] mClasses;
    private final HashMap<Class, Dao> mDaos = new HashMap<>();

    public DatabaseBase(Context context, String databaseName,
                        SQLiteDatabase.CursorFactory factory,
                        int databaseVersion, Class... classes) {
        super(context, databaseName, factory, databaseVersion);
        mContext = context;
        mClasses = classes;
    }

    public DatabaseBase(Context context, String databaseName,
                        SQLiteDatabase.CursorFactory factory,
                        int databaseVersion, int configFileId, Class... classes) {
        super(context, databaseName, factory, databaseVersion, configFileId);
        mContext = context;
        mClasses = classes;
    }

    public DatabaseBase(Context context, String databaseName,
                        SQLiteDatabase.CursorFactory factory,
                        int databaseVersion, File configFile, Class... classes) {
        super(context, databaseName, factory, databaseVersion, configFile);
        mContext = context;
        mClasses = classes;
    }

    public DatabaseBase(Context context, String databaseName,
                        SQLiteDatabase.CursorFactory factory,
                        int databaseVersion, InputStream stream, Class... classes) {
        super(context, databaseName, factory, databaseVersion, stream);
        mContext = context;
        mClasses = classes;
    }

    /**
     * Must be called before database will be used if any database object extend from dependency object.
     *
     * @throws SQLException
     */
    @WorkerThread
    public void initDaos() throws SQLException {
        for (Class clazz : mClasses)
            getDao(clazz);
        removeUnneededData();
    }

    @WorkerThread
    public void removeUnneededData() throws SQLException {
        for (Dao dao : mDaos.values()) {
            if (dao instanceof DependencyDao)
                ((DependencyDao) dao).removeUnneededData();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            for (Class clazz : mClasses) TableUtils.createTable(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (Class clazz : mClasses)
                TableUtils.dropTable(connectionSource, clazz, true);// FIXME: 27.4.16 why we are ignoring errors? (don't ignore errors and filter no table found exception)
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Class[] getDataClasses() {
        return mClasses;
    }

    @Override
    public void close() {
        mDaos.clear();
        super.close();
    }

    @Override
    @WorkerThread
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        //noinspection unchecked
        D result = (D) mDaos.get(clazz);
        if (result == null) {
            //noinspection unchecked
            result = (D) (DependencyDatabaseObject.class.isAssignableFrom(clazz) ?
                    createDependencyDao((Class<? extends DependencyDatabaseObject>) clazz) :
                    new DatabaseObjectDao<>((Dao<T, Long>) super.getDao(clazz),
                            new DatabaseObjectChangeDetector<>(mContext, clazz)));
            mDaos.put(clazz, result);
        }
        return result;
    }

    @WorkerThread
    protected <D extends DependencyDao<T>, T extends DependencyDatabaseObject> D createDependencyDao(Class<T> clazz) throws SQLException {
        Dao<T, Long> dao = super.getDao(clazz);
        DependencyObjectManager<T> dependencyObjectManager;
        try {
            //noinspection unchecked
            dependencyObjectManager = (DependencyObjectManager<T>) clazz
                    .getMethod("getObjectManager", Context.class).invoke(null, mContext);
        } catch (InvocationTargetException | NoSuchMethodException
                | IllegalAccessException | ClassCastException e) {
            Log.d(LOG_TAG, "createDependencyDao - using default DependencyObjectManager, because " +
                    "method getObjectManager() no exists or have wrong implementation for " + clazz);
            dependencyObjectManager = new DependencyObjectManager<>(mContext, clazz);
        }
        dependencyObjectManager.bindDatabase(this);
        //noinspection unchecked
        return (D) new DependencyDao<>(dao, dependencyObjectManager);
    }
}

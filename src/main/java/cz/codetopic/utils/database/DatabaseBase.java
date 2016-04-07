package cz.codetopic.utils.database;

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

import cz.codetopic.utils.Log;

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
            for (Class clazz : mClasses) TableUtils.dropTable(connectionSource, clazz, true);
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

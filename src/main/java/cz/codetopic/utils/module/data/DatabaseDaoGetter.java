package cz.codetopic.utils.module.data;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import cz.codetopic.utils.module.Module;
import cz.codetopic.utils.module.ModuleGetter;

/**
 * Created by anty on 25.2.16.
 *
 * @author anty
 */
public class DatabaseDaoGetter<MT extends Module, DT> extends ModuleGetter<MT> {

    private final Class<DT> mDaoObjectClass;

    public DatabaseDaoGetter(Class<MT> moduleClass, Class<DT> daoObjectClass) {
        super(moduleClass);
        mDaoObjectClass = daoObjectClass;
    }

    @WorkerThread
    public boolean validate() throws SQLException {
        return get() != null;
    }

    @WorkerThread
    public Dao<DT, ?> get() throws SQLException {
        MT module = getModule();
        //noinspection unchecked
        return module == null ? null : module.getDatabaseDao(getDaoObjectClass());
    }

    public ModuleDatabase getDatabase() {
        return getModule().getDatabase();
    }

    public Class<DT> getDaoObjectClass() {
        return mDaoObjectClass;
    }
}

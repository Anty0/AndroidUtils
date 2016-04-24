package eu.codetopic.utils.module.getter;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.JobManager;

import java.sql.SQLException;

import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.data.ModuleDatabase;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public class ModuleDatabaseDaoGetterImpl<MT extends Module, DT> extends ModuleGetterImpl<MT>
        implements ModuleDatabaseDaoGetter<MT, DT> {

    private final Class<DT> mDaoObjectClass;

    public ModuleDatabaseDaoGetterImpl(Class<MT> moduleClass, Class<DT> daoObjectClass) {
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

    @Override
    public JobManager getJobManager() {
        return getModule().getJobManager();
    }

    public Class<DT> getDaoObjectClass() {
        return mDaoObjectClass;
    }
}

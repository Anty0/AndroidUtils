package eu.codetopic.utils.database.singleton;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.JobManager;

import java.sql.SQLException;

import eu.codetopic.utils.database.DatabaseBase;
import eu.codetopic.utils.module.getter.DatabaseDaoGetter;

/**
 * Created by anty on 24.4.16.
 *
 * @author anty
 */
public class SingletonDatabaseDaoGetter<DT> implements DatabaseDaoGetter<DT> {

    private static final String LOG_TAG = "SingletonDatabaseDaoGetter";

    private final Class<DT> mDataClass;

    public SingletonDatabaseDaoGetter(Class<DT> dataClass) {
        mDataClass = dataClass;
    }

    @Override
    public Dao<DT, ?> get() throws SQLException {
        return getDatabase().getDao(getDaoObjectClass());
    }

    @Override
    public Class<DT> getDaoObjectClass() {
        return mDataClass;
    }

    @Override
    public DatabaseBase getDatabase() {
        return SingletonDatabase.getInstance();
    }

    @Override
    public JobManager getJobManager() {
        return SingletonDatabase.getJobManager();
    }
}

package eu.codetopic.utils.data.database.singleton;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.SingletonJobManagerGetter;

public class SingletonDatabaseDaoGetter<DT, ID> extends SingletonJobManagerGetter implements DatabaseDaoGetter<DT, ID> {

    private static final String LOG_TAG = "SingletonDatabaseDaoGetter";

    private final Class<DT> mDataClass;

    public SingletonDatabaseDaoGetter(Class<DT> dataClass) {
        mDataClass = dataClass;
    }

    @Override
    public Dao<DT, ID> get() throws SQLException {
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
}

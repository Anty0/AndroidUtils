package com.codetopic.utils.data.database.singleton;

import com.codetopic.utils.data.database.DatabaseBase;
import com.codetopic.utils.data.getter.DatabaseDaoGetter;
import com.codetopic.utils.thread.job.SingletonJobManagerGetter;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

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

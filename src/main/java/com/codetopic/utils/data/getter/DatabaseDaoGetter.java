package com.codetopic.utils.data.getter;

import android.support.annotation.WorkerThread;

import com.codetopic.utils.data.database.DatabaseBase;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface DatabaseDaoGetter<DT, ID> extends BaseGetter, JobManagerGetter {

    @WorkerThread
    Dao<DT, ID> get() throws SQLException;

    Class<DT> getDaoObjectClass();

    DatabaseBase getDatabase();
}

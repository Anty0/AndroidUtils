package eu.codetopic.utils.module.getter;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.JobManager;

import java.io.Serializable;
import java.sql.SQLException;

import eu.codetopic.utils.database.DatabaseBase;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public interface DatabaseDaoGetter<DT> extends Serializable {

    @WorkerThread
    Dao<DT, ?> get() throws SQLException;

    Class<DT> getDaoObjectClass();

    DatabaseBase getDatabase();

    JobManager getJobManager();
}

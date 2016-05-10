package eu.codetopic.utils.data.getter;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.JobManager;

import java.sql.SQLException;

import eu.codetopic.utils.data.database.DatabaseBase;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public interface DatabaseDaoGetter<DT> extends BaseGetter {

    @WorkerThread
    Dao<DT, ?> get() throws SQLException;

    Class<DT> getDaoObjectClass();

    DatabaseBase getDatabase();

    JobManager getJobManager();
}

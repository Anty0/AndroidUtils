package cz.codetopic.utils.database;

import android.support.annotation.WorkerThread;

import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

/**
 * Created by anty on 22.3.16.
 *
 * @author anty
 */
public abstract class DependencyDatabaseObject extends DatabaseObject {

    @DatabaseField
    private boolean deleted = false;

    public boolean isDeleted() {
        return deleted;
    }

    public void delete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    @WorkerThread
    public abstract boolean isRequired() throws SQLException;// TODO: 5.4.16 Use HoldableDatabaseObjectUtils to implement this method

}

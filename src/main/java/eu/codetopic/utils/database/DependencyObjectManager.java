package eu.codetopic.utils.database;

import android.content.Context;
import android.support.annotation.WorkerThread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DependencyObjectManager<T extends DependencyDatabaseObject> {

    private static final String LOG_TAG = "DependencyObjectManager";

    private final Context mContext;
    private final DatabaseObjectChangeDetector<T> mChangeDetector;
    private DatabaseBase mDatabase = null;

    public DependencyObjectManager(Context context, Class<T> clazz) {
        mContext = context;
        mChangeDetector = new DatabaseObjectChangeDetector<>(context, clazz);
    }

    void bindDatabase(DatabaseBase mDatabase) {
        this.mDatabase = mDatabase;
    }

    protected Class[] getDependencies() {
        return new Class[0];
    }

    protected DependenciesDetector<T> getDependenciesDetector() {
        return new DefaultDependenciesDetector<>();
    }

    @WorkerThread
    public void onUpdateObject(DependencyDao<T> dao, T object) throws SQLException {
        if (mDatabase == null)
            throw new IllegalStateException(LOG_TAG + " is not bind to database");

        if (object instanceof DependencyTextDatabaseObject) {
            ((DependencyTextDatabaseObject) object).updateText(mContext);
            dao.saveWithOutUpdate(object);
        }

        DependenciesDetector<T> dependenciesDetector = getDependenciesDetector();
        for (Class clazz : getDependencies()) {
            //noinspection RedundantCast
            DependencyDao foundDao = (DependencyDao) mDatabase.getDao(clazz);
            for (Object o : foundDao)
                if (o instanceof DependencyTextDatabaseObject &&
                        dependenciesDetector.depends(object, (DependencyTextDatabaseObject) o)) {
                    ((DependencyTextDatabaseObject) o).updateText(mContext);
                    //noinspection unchecked
                    foundDao.saveWithOutUpdate((DependencyTextDatabaseObject) o);
                }
        }

        mChangeDetector.onChange();
    }

    @WorkerThread
    public void onUpdateObjects(DependencyDao<T> dao, Collection<T> objects) throws SQLException {
        if (mDatabase == null)
            throw new IllegalStateException(LOG_TAG + " is not bind to database");

        DependenciesDetector<T> dependenciesDetector = getDependenciesDetector();
        Class[] dependencies = getDependencies();
        DependencyDao[] daos = new DependencyDao[dependencies.length];
        for (int i = 0; i < dependencies.length; i++) {
            //noinspection RedundantCast
            daos[i] = (DependencyDao) mDatabase.getDao(dependencies[i]);
        }

        for (T object : objects) {
            if (object instanceof DependencyTextDatabaseObject) {
                ((DependencyTextDatabaseObject) object).updateText(mContext);
                dao.saveWithOutUpdate(object);
            }

            for (DependencyDao foundDao : daos) {
                for (Object o : foundDao)
                    if (o instanceof DependencyTextDatabaseObject &&
                            dependenciesDetector.depends(object, (DependencyTextDatabaseObject) o)) {
                        ((DependencyTextDatabaseObject) o).updateText(mContext);
                        //noinspection unchecked
                        foundDao.saveWithOutUpdate((DependencyTextDatabaseObject) o);
                    }
            }
        }

        mChangeDetector.onChange();
    }

    @WorkerThread
    public void onUpdateAll(DependencyDao<T> dao) throws SQLException {
        onUpdateObjects(dao, dao.queryForAll());
    }

    @WorkerThread
    public void removeUnneededData(DependencyDao<T> dao) throws SQLException {
        ArrayList<T> toDelete = new ArrayList<>();
        Iterator<T> iterator = dao.iteratorWithTemp();
        while (iterator.hasNext()) {
            T data = iterator.next();
            if (data.isDeleted() && !data.isRequired())
                toDelete.add(data);
        }

        dao.deleteFromTemp(toDelete);
        mChangeDetector.onChange();
    }

    public interface DependenciesDetector<T extends DependencyDatabaseObject> {

        @WorkerThread
        boolean depends(T modified, DependencyTextDatabaseObject toUpdate) throws SQLException;
    }

    public static class DefaultDependenciesDetector<T extends DependencyDatabaseObject>
            implements DependenciesDetector<T> {

        @Override
        public boolean depends(T modified, DependencyTextDatabaseObject toUpdate) {
            return true;
        }
    }

}

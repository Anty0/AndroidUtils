package eu.codetopic.utils.activity.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.path.android.jobqueue.JobManager;

import eu.codetopic.utils.activity.loading.LoadingViewHolderActivity;
import eu.codetopic.utils.data.database.DatabaseObject;
import eu.codetopic.utils.data.database.DependencyTextDatabaseObject;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.DatabaseJob;

public abstract class DatabaseDataActivity<DT extends DatabaseObject> extends LoadingViewHolderActivity {

    private static final String EXTRA_DATA_ID =
            "DatabaseDataActivity.EXTRA_DATA_ID";
    private static final String EXTRA_DAO_GETTER =
            "DatabaseDataActivity.EXTRA_DAO_GETTER";
    private static final String EXTRA_SERIALIZED_DATA =
            "DatabaseDataActivity.EXTRA_DATA_KEY";

    private DatabaseDaoGetter<DT> mDaoGetter = null;
    private JobManager mJobManager;
    private DT mData = null;
    private Long mDataId = null;

    public static <DT extends DatabaseObject> Intent generateDataActivityIntent
            (Context context, Class<? extends DatabaseDataActivity<DT>> clazz,
             @NonNull DatabaseDaoGetter<DT> daoGetter, @Nullable DT data) {
        return new Intent(context, clazz)
                .putExtra(EXTRA_DATA_ID, data == null ? null : data.getId())
                .putExtra(EXTRA_DAO_GETTER, daoGetter);
    }

    public static <DT extends DatabaseObject> void startDataActivity
            (Context context, Class<? extends DatabaseDataActivity<DT>> clazz,
             @NonNull DatabaseDaoGetter<DT> daoGetter, @Nullable DT data) {
        context.startActivity(generateDataActivityIntent(context, clazz, daoGetter, data));
    }

    public static <DT extends DatabaseObject> void startDataActivityForResult
            (Activity activity, int requestCode, Class<? extends DatabaseDataActivity<DT>> clazz,
             @NonNull DatabaseDaoGetter<DT> daoGetter, @Nullable DT data) {
        activity.startActivityForResult(generateDataActivityIntent(activity, clazz, daoGetter, data), requestCode);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataId = (Long) getIntent().getSerializableExtra(EXTRA_DATA_ID);
        mDaoGetter = (DatabaseDaoGetter<DT>) getIntent().getSerializableExtra(EXTRA_DAO_GETTER);
        mJobManager = mDaoGetter.getJobManager();
        if (mJobManager == null) throw new NullPointerException("Module must have JobManager");


        reloadData(savedInstanceState);
    }

    @WorkerThread
    protected DT loadData(Dao<DT, Long> dao, @Nullable Bundle savedInstanceState) throws Throwable {
        if (savedInstanceState == null) {
            if (mDataId == null) {
                DT data = mDaoGetter.getDaoObjectClass().newInstance();
                if (data instanceof DependencyTextDatabaseObject)
                    ((DependencyTextDatabaseObject) data).updateText(this);
                return data;
            }
            return dao.queryForId(mDataId);
        }
        //noinspection unchecked
        return (DT) savedInstanceState.getSerializable(EXTRA_SERIALIZED_DATA);
    }

    protected void reloadData() {
        reloadData(null);
    }

    private void reloadData(final @Nullable Bundle savedInstanceState) {
        mJobManager.addJob(new DatabaseJob<>(getLoadingViewHolder(), mDaoGetter, new DatabaseJob.DatabaseWork<DT, Long>() {
            @Override
            public void run(Dao<DT, Long> dao) throws Throwable {
                mData = loadData(dao, savedInstanceState);

                JobUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onDataLoaded(savedInstanceState == null, mData);
                    }
                });
            }
        }));
    }

    protected void saveData() {
        if (onBeforeSaveData(getData()))
            mJobManager.addJob(new DatabaseJob<>(mDaoGetter,
                    DatabaseJob.Modification.CREATE_OR_UPDATE, mData));
    }

    protected abstract void onDataLoaded(boolean isFirstLoad, DT data);

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        DT data = getData();
        onBeforeSaveData(data);
        outState.putSerializable(EXTRA_SERIALIZED_DATA, data);
        super.onSaveInstanceState(outState);
    }

    protected abstract boolean onBeforeSaveData(DT data);

    @Override
    @Deprecated
    public void finish() {
        finish(false);
    }

    public void finish(boolean saveData) {
        super.finish();
        if (saveData) saveData();
    }

    public DT getData() {
        return mData;
    }

    public DatabaseDaoGetter<DT> getDaoGetter() {
        return mDaoGetter;
    }
}

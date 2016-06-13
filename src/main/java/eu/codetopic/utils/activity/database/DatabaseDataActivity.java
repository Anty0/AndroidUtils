package eu.codetopic.utils.activity.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;

import eu.codetopic.utils.activity.loading.LoadingModularActivity;
import eu.codetopic.utils.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.data.database.DatabaseObject;
import eu.codetopic.utils.data.database.DependencyTextDatabaseObject;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.database.DbJob;

public abstract class DatabaseDataActivity<DT extends DatabaseObject, ID> extends LoadingModularActivity {

    private static final String EXTRA_DATA_ID =
            "DatabaseDataActivity.EXTRA_DATA_ID";
    private static final String EXTRA_DAO_GETTER =
            "DatabaseDataActivity.EXTRA_DAO_GETTER";
    private static final String EXTRA_SERIALIZED_DATA =
            "DatabaseDataActivity.EXTRA_DATA_KEY";

    private DatabaseDaoGetter<DT, ID> mDaoGetter = null;
    private DT mData = null;
    private ID mDataId = null;

    public DatabaseDataActivity(ActivityCallBackModule... modules) {
        super(modules);
    }

    public static <DT extends DatabaseObject, ID> Intent generateDataActivityIntent
            (Context context, Class<? extends DatabaseDataActivity<DT, ID>> clazz,
             @NonNull DatabaseDaoGetter<DT, ID> daoGetter, @Nullable DT data) {
        return new Intent(context, clazz)
                .putExtra(EXTRA_DATA_ID, data == null ? null : data.getId())
                .putExtra(EXTRA_DAO_GETTER, daoGetter);
    }

    public static <DT extends DatabaseObject, ID> void startDataActivity
            (Context context, Class<? extends DatabaseDataActivity<DT, ID>> clazz,
             @NonNull DatabaseDaoGetter<DT, ID> daoGetter, @Nullable DT data) {
        context.startActivity(generateDataActivityIntent(context, clazz, daoGetter, data));
    }

    public static <DT extends DatabaseObject, ID> void startDataActivityForResult
            (Activity activity, int requestCode, Class<? extends DatabaseDataActivity<DT, ID>> clazz,
             @NonNull DatabaseDaoGetter<DT, ID> daoGetter, @Nullable DT data) {
        activity.startActivityForResult(generateDataActivityIntent(activity, clazz, daoGetter, data), requestCode);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataId = (ID) getIntent().getSerializableExtra(EXTRA_DATA_ID);
        mDaoGetter = (DatabaseDaoGetter<DT, ID>) getIntent().getSerializableExtra(EXTRA_DAO_GETTER);

        reloadData(savedInstanceState);
    }

    @WorkerThread
    protected DT loadData(Dao<DT, ID> dao, @Nullable Bundle savedInstanceState) throws Throwable {
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
        DbJob.work(mDaoGetter).startCallback(new DbJob.CallbackWork<DT, DT, ID>() {
            @WorkerThread
            @Override
            public DT run(Dao<DT, ID> dao) throws Throwable {
                return loadData(dao, savedInstanceState);// FIXME: 28.5.16 leak
            }
        }, new DbJob.Callback<DT>() {
            @Override
            public void onResult(DT result) {
                onDataLoaded(savedInstanceState == null, mData);// FIXME: 28.5.16 leak
            }
        });
    }

    protected void saveData() {
        if (onBeforeSaveData(getData()))
            DbJob.work(mDaoGetter).save(mData);
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

    public DatabaseDaoGetter<DT, ID> getDaoGetter() {
        return mDaoGetter;
    }
}

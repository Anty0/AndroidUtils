/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.activity.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;

import eu.codetopic.utils.data.database.DatabaseObject;
import eu.codetopic.utils.data.database.DependencyTextDatabaseObject;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.database.DbJob;
import eu.codetopic.utils.ui.activity.modular.ActivityCallBackModule;
import eu.codetopic.utils.ui.view.holder.loading.LoadingModularActivity;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

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

    public DatabaseDataActivity() {
    }

    public DatabaseDataActivity(ActivityCallBackModule... modules) {
        super(modules);
    }

    public DatabaseDataActivity(Class<? extends LoadingVH> holderClass, ActivityCallBackModule... modules) {
        super(holderClass, modules);
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

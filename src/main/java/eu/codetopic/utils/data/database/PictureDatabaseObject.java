package eu.codetopic.utils.data.database;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.callback.CallbackUtils;
import eu.codetopic.utils.data.database.holder.BitmapDatabaseObject;
import eu.codetopic.utils.data.database.holder.BitmapHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.database.DatabaseWork;
import eu.codetopic.utils.thread.job.database.DbJob;

public abstract class PictureDatabaseObject<BDO extends BitmapDatabaseObject,
        BH extends BitmapHolder<BDO>> extends DatabaseObject {

    private static final String LOG_TAG = "PictureDatabaseObject";

    private final DatabaseDaoGetter<BDO> bdoGetter;
    private final Class<BH> bhClass;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private BH picture;

    public PictureDatabaseObject(DatabaseDaoGetter<BDO> bitmapDatabaseObjectDaoGetter,
                                 Class<BH> bitmapHolderClass) {
        bdoGetter = bitmapDatabaseObjectDaoGetter;
        bhClass = bitmapHolderClass;
    }

    public void getPictureOnBackground(final ActionCallback<Bitmap> callback) {
        DbJob.work(bdoGetter).start(new DatabaseWork<BDO, Long>() {
            @Override
            public void run(Dao<BDO, Long> dao) throws Throwable {
                CallbackUtils.doCallbackWorkWithThrow(callback, new CallbackUtils.CallbackWork<Bitmap>() {
                    @Override
                    public Bitmap work() throws Throwable {
                        return getPicture();
                    }
                });
            }
        });
    }

    @WorkerThread
    public Bitmap getPicture() throws SQLException {
        return picture == null ? null : picture.getBitmap();
    }

    @WorkerThread
    public void setPicture(Bitmap picture) throws SQLException {
        if (this.picture == null) {
            try {
                this.picture = BitmapHolder.create(bdoGetter, bhClass, picture);
            } catch (Exception e) {
                if (e instanceof SQLException) throw (SQLException) e;
                Log.e(LOG_TAG, "setPicture", e);
            }
            return;
        }
        this.picture.setBitmap(picture);
    }

    public void setPictureOnBackground(final Bitmap picture, @Nullable final ActionCallback<Bitmap> callback) {
        DbJob.work(bdoGetter).start(new DatabaseWork<BDO, Long>() {
            @Override
            public void run(Dao<BDO, Long> dao) throws Throwable {
                CallbackUtils.doCallbackWorkWithThrow(callback, new CallbackUtils.CallbackWork<Bitmap>() {
                    @Override
                    public Bitmap work() throws Throwable {
                        setPicture(picture);
                        return picture;
                    }
                });
            }
        });
    }
}

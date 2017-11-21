/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.callback.CallbackUtils;
import eu.codetopic.utils.data.database.holder.BitmapDatabaseObject;
import eu.codetopic.utils.data.database.holder.BitmapHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.database.DatabaseWork;
import eu.codetopic.utils.thread.job.database.DbJob;
import eu.codetopic.utils.ui.container.items.multiline.MultilineLoadableImageItem;

public abstract class DependencyPictureTextDatabaseObject<BDO extends BitmapDatabaseObject,
        BH extends BitmapHolder<BDO>> extends DependencyTextDatabaseObject implements MultilineLoadableImageItem {

    private static final String LOG_TAG = "DependencyPictureTextDatabaseObject";

    private final DatabaseDaoGetter<BDO, Long> bdoGetter;
    private final Class<BH> bhClass;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private BH picture;

    public DependencyPictureTextDatabaseObject(DatabaseDaoGetter<BDO, Long> bitmapDatabaseObjectDaoGetter,
                                               Class<BH> bitmapHolderClass) {
        bdoGetter = bitmapDatabaseObjectDaoGetter;
        bhClass = bitmapHolderClass;
    }

    public void getPictureOnBackground(Context context, ActionCallback<Bitmap> callback) {
        getPictureOnBackground(new WeakReference<>(context), callback);
    }

    public void getPictureOnBackground(final WeakReference<Context> contextRef,
                                       final ActionCallback<Bitmap> callback) {
        DbJob.work(bdoGetter).start(new DatabaseWork<BDO, Long>() {
            @Override
            public void run(Dao<BDO, Long> dao) throws Throwable {
                CallbackUtils.doCallbackWorkWithThrow(contextRef, callback, new CallbackUtils.CallbackWork<Bitmap>() {
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

    public void setPictureOnBackground(Bitmap picture, Context context,
                                       @Nullable ActionCallback<Bitmap> callback) {
        setPictureOnBackground(picture, new WeakReference<>(context), callback);
    }

    public void setPictureOnBackground(final Bitmap picture, final WeakReference<Context> contextRef,
                                       @Nullable final ActionCallback<Bitmap> callback) {
        DbJob.work(bdoGetter).start(new DatabaseWork<BDO, Long>() {
            @Override
            public void run(Dao<BDO, Long> dao) throws Throwable {
                CallbackUtils.doCallbackWorkWithThrow(contextRef, callback, new CallbackUtils.CallbackWork<Bitmap>() {
                    @Override
                    public Bitmap work() throws Throwable {
                        setPicture(picture);
                        return picture;
                    }
                });
            }
        });
    }

    @Override
    public void loadImage(Context context, int position, final ActionCallback<Bitmap> callback) {
        getPictureOnBackground(context, callback);
    }

}

package cz.codetopic.utils.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.database.holder.BitmapDatabaseObject;
import cz.codetopic.utils.database.holder.BitmapHolder;
import cz.codetopic.utils.module.data.DatabaseDaoGetter;

/**
 * Created by anty on 4.4.16.
 *
 * @author anty
 */
public abstract class DependencyPictureTextDatabaseObject<BDO extends BitmapDatabaseObject,
        BH extends BitmapHolder<BDO>> extends DependencyTextDatabaseObject {

    private static final String LOG_TAG = "DependencyPictureTextDatabaseObject";

    private final DatabaseDaoGetter<?, BDO> bdoGetter;
    private final Class<BH> bhClass;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private BH picture;

    public DependencyPictureTextDatabaseObject(DatabaseDaoGetter<?, BDO> bitmapDatabaseObjectDaoGetter, Class<BH> bitmapHolderClass) {
        bdoGetter = bitmapDatabaseObjectDaoGetter;
        bhClass = bitmapHolderClass;
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

    @WorkerThread
    public Bitmap getImageBitmapOnBackground(Context context, int position) throws SQLException {
        return getPicture();// TODO: 4.4.16 Add to multiline item support for loadable bitmaps
    }

}

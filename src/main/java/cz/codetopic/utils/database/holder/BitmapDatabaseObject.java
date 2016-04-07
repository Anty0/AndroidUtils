package cz.codetopic.utils.database.holder;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

import cz.codetopic.utils.Utils;
import cz.codetopic.utils.database.DatabaseBase;
import cz.codetopic.utils.database.DependencyDatabaseObject;

/**
 * Created by anty on 4.4.16.
 *
 * @author anty
 */
public abstract class BitmapDatabaseObject extends DependencyDatabaseObject {

    private static final String LOG_TAG = "BitmapDatabaseObject";

    private final Class<? extends BitmapHolder<?>> bitmapHolderClass;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] bitmapBytes = null;

    protected BitmapDatabaseObject(Class<? extends BitmapHolder<?>> bitmapHolderClass) {
        this.bitmapHolderClass = bitmapHolderClass;
    }

    public Bitmap getBitmap() {
        if (bitmapBytes == null) return null;
        return Utils.getBitmapFromBytes(bitmapBytes);
    }

    public void setBitmap(Bitmap picture) {
        bitmapBytes = picture == null ? null : Utils.getBitmapBytes(picture);
    }

    protected abstract DatabaseBase getDatabase();

    @Override
    public boolean isRequired() throws SQLException {
        return HoldableDatabaseObjectUtils.isRequired(getDatabase(), this, bitmapHolderClass);
    }
}

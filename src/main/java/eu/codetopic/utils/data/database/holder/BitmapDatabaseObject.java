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

package eu.codetopic.utils.data.database.holder;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.sql.SQLException;

import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.database.DependencyDatabaseObject;

public abstract class BitmapDatabaseObject extends DependencyDatabaseObject {

    private static final String LOG_TAG = "BitmapDatabaseObject";

    private final Class<? extends BitmapHolder<?>> bitmapHolderClass;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] bitmapBytes = null;

    public BitmapDatabaseObject(Class<? extends BitmapHolder<?>> bitmapHolderClass) {
        this.bitmapHolderClass = bitmapHolderClass;
    }

    public Bitmap getBitmap() {
        if (bitmapBytes == null) return null;
        return AndroidUtils.getBitmapFromBytes(bitmapBytes);
    }

    public void setBitmap(Bitmap picture) {
        bitmapBytes = picture == null ? null : AndroidUtils.getBitmapBytes(picture);
    }

    protected abstract DatabaseBase getDatabase();

    @Override
    public boolean isDeleted() {
        return true;
    }

    @Override
    public boolean isRequired() throws SQLException {
        return HoldableDatabaseObjectUtils.isRequired(getDatabase(), this, bitmapHolderClass);
    }
}

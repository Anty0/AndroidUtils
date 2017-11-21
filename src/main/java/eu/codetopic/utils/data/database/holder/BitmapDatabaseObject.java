/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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

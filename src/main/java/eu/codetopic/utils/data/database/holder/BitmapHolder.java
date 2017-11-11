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

package eu.codetopic.utils.data.database.holder;

import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public abstract class BitmapHolder<T extends BitmapDatabaseObject> extends DatabaseObjectHolder<T> {

    private Bitmap bitmap = null;

    /**
     * @hide
     */
    @Keep
    public BitmapHolder() {
    }

    public BitmapHolder(Long objectId) {
        super(objectId);
    }

    @WorkerThread
    public static <BH extends BitmapHolder<T>, T extends BitmapDatabaseObject> BH create
            (DatabaseDaoGetter<T, Long> daoGetter, Class<BH> holderClass, Bitmap bitmap)
            throws IllegalAccessException, InstantiationException, SQLException,
            NoSuchMethodException, InvocationTargetException {
        T obj = daoGetter.getDaoObjectClass().newInstance();
        obj.setBitmap(bitmap);
        daoGetter.get().create(obj);
        return holderClass.getConstructor(Long.class).newInstance(obj.getId());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

    }

    @WorkerThread
    public Bitmap getBitmap() throws SQLException {
        if (bitmap == null) bitmap = getObject().getBitmap();
        return bitmap;
    }

    @WorkerThread
    public void setBitmap(Bitmap bitmap) throws SQLException {
        this.bitmap = bitmap;
        T object = getObject();
        object.setBitmap(bitmap);
        getDaoGetter().get().update(object);
    }

    @proguard.annotation.Keep
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        //keep method for serializable compatibility
    }
}

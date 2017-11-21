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

package eu.codetopic.utils.data.database.holder;

import android.graphics.Bitmap;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

/**
 * Created by anty on 4.4.16.
 *
 * @author anty
 */
public abstract class BitmapHolder<T extends BitmapDatabaseObject> extends DatabaseObjectHolder<T> {

    private Bitmap bitmap = null;

    public BitmapHolder() {
    }

    public BitmapHolder(Long objectId) {
        super(objectId);
    }

    @WorkerThread
    public static <BH extends BitmapHolder<T>, T extends BitmapDatabaseObject> BH create
            (DatabaseDaoGetter<T> daoGetter, Class<BH> holderClass, Bitmap bitmap)
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
        if (this.bitmap != null) this.bitmap.recycle();
        this.bitmap = bitmap;
        T object = getObject();
        object.setBitmap(bitmap);
        getDaoGetter().get().update(object);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (bitmap != null) bitmap.recycle();
    }
}

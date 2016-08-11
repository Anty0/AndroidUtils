package eu.codetopic.utils.data.database.singleton.holder;

import android.support.annotation.Keep;

import eu.codetopic.utils.data.database.holder.BitmapHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public class BitmapHolderSingletonImpl extends BitmapHolder<BitmapDatabaseObjectSingletonImpl> {

    /**
     * @hide
     */
    @Keep
    public BitmapHolderSingletonImpl() {
    }

    @Keep
    public BitmapHolderSingletonImpl(Long objectId) {
        super(objectId);
    }

    @Override
    public DatabaseDaoGetter<BitmapDatabaseObjectSingletonImpl, Long> getDaoGetter() {
        return BitmapDatabaseObjectSingletonImpl.getter;
    }
}

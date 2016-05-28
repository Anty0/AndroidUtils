package eu.codetopic.utils.data.database.singleton.holder;

import eu.codetopic.utils.data.database.holder.BitmapHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public class BitmapHolderSingletonImpl extends BitmapHolder<BitmapDatabaseObjectSingletonImpl> {

    public BitmapHolderSingletonImpl() {
    }

    public BitmapHolderSingletonImpl(Long objectId) {
        super(objectId);
    }

    @Override
    public DatabaseDaoGetter<BitmapDatabaseObjectSingletonImpl> getDaoGetter() {
        return BitmapDatabaseObjectSingletonImpl.getter;
    }
}

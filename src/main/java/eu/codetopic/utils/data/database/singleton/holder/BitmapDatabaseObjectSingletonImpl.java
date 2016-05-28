package eu.codetopic.utils.data.database.singleton.holder;

import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.database.holder.BitmapDatabaseObject;
import eu.codetopic.utils.data.database.singleton.SingletonDatabase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public class BitmapDatabaseObjectSingletonImpl extends BitmapDatabaseObject {

    public static DatabaseDaoGetter<BitmapDatabaseObjectSingletonImpl> getter =
            SingletonDatabase.getGetterFor(BitmapDatabaseObjectSingletonImpl.class);

    protected BitmapDatabaseObjectSingletonImpl() {
        super(BitmapHolderSingletonImpl.class);
    }

    @Override
    protected DatabaseBase getDatabase() {
        return SingletonDatabase.getInstance();
    }
}

package eu.codetopic.utils.data.database.singleton.holder;

import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.database.holder.BitmapDatabaseObject;
import eu.codetopic.utils.data.database.singleton.SingletonDatabase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import proguard.annotation.Keep;
import proguard.annotation.KeepClassMemberNames;
import proguard.annotation.KeepClassMembers;
import proguard.annotation.KeepName;

@Keep
@KeepName
@KeepClassMembers
@KeepClassMemberNames
public class BitmapDatabaseObjectSingletonImpl extends BitmapDatabaseObject {

    public static DatabaseDaoGetter<BitmapDatabaseObjectSingletonImpl, Long> getter =
            SingletonDatabase.getGetterFor(BitmapDatabaseObjectSingletonImpl.class);

    public BitmapDatabaseObjectSingletonImpl() {
        super(BitmapHolderSingletonImpl.class);
    }

    @Override
    protected DatabaseBase getDatabase() {
        return SingletonDatabase.getInstance();
    }
}

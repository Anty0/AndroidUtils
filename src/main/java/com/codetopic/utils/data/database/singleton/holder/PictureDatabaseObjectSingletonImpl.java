package com.codetopic.utils.data.database.singleton.holder;

import com.codetopic.utils.data.database.PictureDatabaseObject;

public class PictureDatabaseObjectSingletonImpl extends
        PictureDatabaseObject<BitmapDatabaseObjectSingletonImpl, BitmapHolderSingletonImpl> {

    public PictureDatabaseObjectSingletonImpl() {
        super(BitmapDatabaseObjectSingletonImpl.getter, BitmapHolderSingletonImpl.class);
    }
}

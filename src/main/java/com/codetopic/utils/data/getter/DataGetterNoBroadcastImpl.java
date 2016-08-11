package com.codetopic.utils.data.getter;

public abstract class DataGetterNoBroadcastImpl<DT> implements DataGetter<DT> {

    @Override
    public boolean hasDataChangedBroadcastAction() {
        return false;
    }

    @Override
    public String getDataChangedBroadcastAction() {
        return null;
    }

}

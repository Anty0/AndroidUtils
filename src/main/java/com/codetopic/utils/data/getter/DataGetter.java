package com.codetopic.utils.data.getter;

public interface DataGetter<DT> extends BaseGetter {

    DT get();

    Class<DT> getDataClass();

    boolean hasDataChangedBroadcastAction();

    String getDataChangedBroadcastAction();
}

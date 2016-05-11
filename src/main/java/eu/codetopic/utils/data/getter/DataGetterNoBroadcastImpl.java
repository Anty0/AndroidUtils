package eu.codetopic.utils.data.getter;

/**
 * Created by anty on 11.5.16.
 *
 * @author anty
 */
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

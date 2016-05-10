package eu.codetopic.utils.data.getter;

/**
 * Created by anty on 23.4.16.
 *
 * @author anty
 */
public interface DataGetter<DT> extends BaseGetter {

    DT get();

    Class<DT> getDataClass();

    boolean hasDataChangedBroadcastAction();

    String getDataChangedBroadcastAction();
}

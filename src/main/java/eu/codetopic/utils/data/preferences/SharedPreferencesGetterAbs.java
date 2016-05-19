package eu.codetopic.utils.data.preferences;

import eu.codetopic.utils.data.getter.DataGetter;

public abstract class SharedPreferencesGetterAbs<DT extends SharedPreferencesData>
        implements DataGetter<DT> {

    @Override
    public boolean hasDataChangedBroadcastAction() {
        return true;
    }

    @Override
    public String getDataChangedBroadcastAction() {
        return SharedPreferencesData.getBroadcastActionChanged(get());
    }
}

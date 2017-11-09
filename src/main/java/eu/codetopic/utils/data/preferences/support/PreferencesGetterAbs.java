package eu.codetopic.utils.data.preferences.support;

import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.PreferencesData;

public abstract class PreferencesGetterAbs<DT extends PreferencesData>
        implements DataGetter<DT> {

    @Override
    public boolean hasDataChangedBroadcastAction() {
        return true;
    }

    @Override
    public String getDataChangedBroadcastAction() {
        return get().getBroadcastActionChanged();
    }
}

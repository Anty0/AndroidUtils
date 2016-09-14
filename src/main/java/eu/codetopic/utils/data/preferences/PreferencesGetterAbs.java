package eu.codetopic.utils.data.preferences;

import eu.codetopic.utils.data.getter.DataGetter;

public abstract class PreferencesGetterAbs<DT extends PreferencesData>
        implements DataGetter<DT> {

    @Override
    public boolean hasDataChangedBroadcastAction() {
        return true;
    }

    @Override
    public String getDataChangedBroadcastAction() {
        return PreferencesData.getBroadcastActionChanged(get());
    }
}

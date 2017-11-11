package eu.codetopic.utils.data.preferences.support;

import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.IPreferencesData;

public abstract class PreferencesGetterAbs<DT extends IPreferencesData>
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

package eu.codetopic.utils.timing.info;

import android.content.ComponentName;
import android.content.Context;

import java.io.Serializable;

import eu.codetopic.utils.Utils;

public class TimCompInfo implements Serializable {

    private final Class<?> mComponent;
    private final TimCompInfoData mComponentInfo;

    /**
     * @hide
     */
    public TimCompInfo(Class<?> componentClass) {
        mComponent = componentClass;
        mComponentInfo = new TimCompInfoData(componentClass);
    }

    public Class<?> getComponentClass() {
        return mComponent;
    }

    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, mComponent);
    }

    public boolean isEnabled(Context context) {
        return Utils.isComponentEnabled(context, mComponent);
    }

    public TimCompInfoData getComponentInfo() {
        return mComponentInfo;
    }
}

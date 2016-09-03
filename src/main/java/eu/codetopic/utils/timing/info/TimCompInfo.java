package eu.codetopic.utils.timing.info;

import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;

import eu.codetopic.utils.Utils;

public final class TimCompInfo {

    private final Class<?> mComponent;
    private final TimCompInfoData mComponentInfo;

    /**
     * @hide
     */
    @NonNull
    public static TimCompInfo createInfoFor(Context context, Class<?> componentClass) {
        return new TimCompInfo(context, componentClass);
    }

    private TimCompInfo(Context context, Class<?> componentClass) {
        mComponent = componentClass;
        mComponentInfo = new TimCompInfoData(context, componentClass);
    }

    public Class<?> getComponentClass() {
        return mComponent;
    }

    @NonNull
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, mComponent);
    }

    public boolean isEnabled(Context context) {
        return Utils.isComponentEnabled(context, mComponent);
    }

    public TimCompInfoData getComponentProperties() {
        return mComponentInfo;
    }

    @Override
    public String toString() {
        return "TimCompInfo{" +
                "mComponent=" + mComponent +
                ", mComponentInfo=" + mComponentInfo +
                '}';
    }
}

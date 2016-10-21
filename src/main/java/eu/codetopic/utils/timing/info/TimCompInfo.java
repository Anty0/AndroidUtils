package eu.codetopic.utils.timing.info;

import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;

import eu.codetopic.utils.AndroidUtils;
import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.timing.TimedComponentsManager;
import eu.codetopic.utils.timing.TimingData;

public final class TimCompInfo {

    private final Class<?> mComponent;
    private final TimCompInfoData mComponentProperties;

    private TimCompInfo(Context context, Class<?> componentClass) {
        mComponent = componentClass;
        mComponentProperties = new TimCompInfoData(context, componentClass);
    }

    /**
     * @hide
     */
    @NonNull
    public static TimCompInfo createInfoFor(Context context, Class<?> componentClass) {
        return new TimCompInfo(context, componentClass);
    }

    public Class<?> getComponentClass() {
        return mComponent;
    }

    @NonNull
    public ComponentName getComponentName(Context context) {
        return new ComponentName(context, mComponent);
    }

    public boolean isEnabled(Context context) {
        return AndroidUtils.isComponentEnabled(context, mComponent);
    }

    public boolean isReady() {
        TimedComponentsManager timCompMan = TimedComponentsManager.getInstance();
        return isReady(timCompMan.getContext(), timCompMan.getRequiredNetwork());
    }

    public boolean isReady(Context context, NetworkManager.NetworkType requiredNetwork) {
        return isEnabled(context)
                && (!mComponentProperties.isRequiresInternetAccess()
                || NetworkManager.isConnected(requiredNetwork));
    }

    public boolean isActive() {
        TimedComponentsManager timCompMan = TimedComponentsManager.getInstance();
        return isActive(timCompMan.getContext(), timCompMan.getRequiredNetwork());
    }

    public boolean isActive(Context context, NetworkManager.NetworkType requiredNetwork) {
        return isReady(context, requiredNetwork) && mComponentProperties.isCurrentTimeInTimeRange();
    }

    public TimCompInfoData getComponentProperties() {
        return mComponentProperties;
    }

    @Override
    public String toString() {
        return "TimCompInfo{" +
                "mComponent=" + mComponent +
                ", mComponentEnabled=" + (!TimedComponentsManager.isInitialized() ? "unknown"
                : isEnabled(TimedComponentsManager.getInstance().getContext())) +
                ", mLastExecuteTime=" + TimingData.getter.get().getLastExecuteTime(mComponent) +
                ", mLastRequestCode=" + TimingData.getter.get().getLastRequestCode(mComponent) +
                ", mComponentProperties=" + mComponentProperties +
                '}';
    }
}

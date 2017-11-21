/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

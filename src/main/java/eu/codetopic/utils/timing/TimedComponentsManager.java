package eu.codetopic.utils.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.HashMap;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.notifications.manage.NotificationIdsManager;
import eu.codetopic.utils.timing.info.TimCompInfo;
import eu.codetopic.utils.timing.info.TimCompInfoData;
import eu.codetopic.utils.timing.info.TimedComponent;

public class TimedComponentsManager {

    public static final String ACTION_TIMED_EXECUTE = "eu.codetopic.utils.timing.TimedComponentsManager.ACTION_TIMED_EXECUTE";
    public static final String ACTION_FORCED_EXECUTE = "eu.codetopic.utils.timing.TimedComponentsManager.ACTION_FORCED_EXECUTE";
    private static final String ACTION_RELOAD_COMPONENT = "eu.codetopic.utils.timing.TimedComponentsManager.RELOAD_COMPONENT";
    private static final String EXTRA_TIMED_COMPONENT_INFO = "eu.codetopic.utils.timing.TimedComponentsManager.TIMED_COMPONENT_INFO";

    private static final String LOG_TAG = "TimedComponentsManager";
    private static TimedComponentsManager INSTANCE = null;

    private final Context mContext;
    private final HashMap<Class<?>, TimCompInfo> mComponentsInfoMap;
    private NetworkManager.NetworkType mRequiredNetwork;

    private TimedComponentsManager(Context context, NetworkManager.NetworkType requiredNetwork,
                                   Class<?>[] components) {

        mContext = context;
        mRequiredNetwork = requiredNetwork;

        if (!NotificationIdsManager.isInitialized())
            throw new IllegalStateException("NotificationIdsManager is not initialized, please initialize it");

        mComponentsInfoMap = new HashMap<>(components.length);
        Log.d(LOG_TAG, "<init> initialising for: " + java.util.Arrays.toString(components));
        synchronized (mComponentsInfoMap) {
            for (Class<?> component : components)
                try {
                    mComponentsInfoMap.put(component, new TimCompInfo(component));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "<init>", t);
                }
        }
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context          application context
     * @param timedComponents  Classes of timed components to use in TimedComponentsManager
     */
    public static void initialize(Context context, Class<?>... timedComponents) {
        initialize(context, NetworkManager.NetworkType.ANY, timedComponents);
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context         application context
     * @param requiredNetwork required network to execute timed components that requires internet access
     * @param timedComponents Classes of timed components to use in TimedComponentsManager
     */
    public static void initialize(Context context, NetworkManager.NetworkType requiredNetwork,
                                  Class<?>... timedComponents) {// TODO: 26.3.16 initialize in ApplicationBase
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        context = context.getApplicationContext();
        TimingData.initialize(context);

        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootConnectivityReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        INSTANCE = new TimedComponentsManager(context, requiredNetwork, timedComponents);

        if (TimingData.getter.get().isFirstLoad())
            INSTANCE.reloadAll();// TODO: 30.5.16 maybe do it every time (every application start)
    }

    public static TimedComponentsManager getInstance() {
        return INSTANCE;
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    void notifyIntentReceived(Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                TimingData data = TimingData.getter.get();
                synchronized (mComponentsInfoMap) {
                    for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                        if (componentInfo.getComponentInfo().isResetRepeatingOnBoot())
                            data.clear(componentInfo.getComponentClass());

                    reloadAll();
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                reloadAllNetwork();
                break;
            case ACTION_RELOAD_COMPONENT:
                TimCompInfo componentInfo = (TimCompInfo) intent
                        .getSerializableExtra(EXTRA_TIMED_COMPONENT_INFO);
                try {
                    reload(componentInfo);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "reload - problem detected while reloading timed component: "
                            + componentInfo.getComponentClass().getName(), e);
                }
                break;
        }
    }

    public NetworkManager.NetworkType getRequiredNetwork() {
        return mRequiredNetwork;
    }

    public void setRequiredNetwork(NetworkManager.NetworkType requiredNetwork) {
        this.mRequiredNetwork = requiredNetwork;
        reloadAllNetwork();
    }

    public void setComponentEnabled(Class<?> componentClass, boolean enabled) {
        TimCompInfo componentInfo = getComponentInfo(componentClass);
        if (componentInfo == null)
            throw new NullPointerException(componentClass.getName() + " no found");
        setComponentEnabled(componentInfo, enabled);
    }

    public void setComponentEnabled(TimCompInfo componentInfo, boolean enabled) {
        PackageManager pm = mContext.getPackageManager();
        if (pm.getComponentEnabledSetting(componentInfo.getComponentName(mContext))
                != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                && enabled == componentInfo.isEnabled(mContext)) return;

        pm.setComponentEnabledSetting(componentInfo.getComponentName(mContext),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        reload(componentInfo);
    }

    public Intent getReloadIntent(Class<?> componentClass) {
        TimCompInfo componentInfo = getComponentInfo(componentClass);
        if (componentInfo == null)
            throw new NullPointerException(componentClass.getName() + " no found");
        return getReloadIntent(componentInfo);
    }

    public Intent getReloadIntent(TimCompInfo componentInfo) {
        return new Intent(mContext, BootConnectivityReceiver.class)
                .setAction(ACTION_RELOAD_COMPONENT)
                .putExtra(EXTRA_TIMED_COMPONENT_INFO, componentInfo);
    }

    public void reloadAllNetwork() {
        synchronized (mComponentsInfoMap) {
            for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                if (componentInfo.getComponentInfo().isRequiresInternetAccess())
                    try {
                        reload(componentInfo);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "reload - problem detected while reloading timed component: "
                                + componentInfo.getComponentClass().getName(), e);
                    }
        }
    }

    public void reloadAll() {
        synchronized (mComponentsInfoMap) {
            for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                try {
                    reload(componentInfo);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "reloadAll - problem detected while reloading timed component: "
                            + componentInfo.getComponentClass().getName(), e);
                }
        }
    }

    public void reload(Class<?> componentClass) {
        synchronized (mComponentsInfoMap) {
            TimCompInfo componentInfo = getComponentInfo(componentClass);
            if (componentInfo == null)
                throw new NullPointerException(componentClass.getName() + " no found");
            reload(componentInfo);
        }
    }

    public void reload(TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMap) {
            AlarmManager alarms = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent componentIntent = TimedComponentExecutor.generateIntent(mContext,
                    ACTION_TIMED_EXECUTE, componentInfo, null);
            Intent reloadIntent = getReloadIntent(componentInfo);

            TimingData data = TimingData.getter.get();
            TimCompInfoData component = componentInfo.getComponentInfo();
            int lastRequestCode = data.getLastRequestCode(componentInfo.getComponentClass());
            if (lastRequestCode != -1) {
                alarms.cancel(PendingIntent.getBroadcast(mContext, lastRequestCode, componentIntent, 0));
                alarms.cancel(PendingIntent.getBroadcast(mContext, lastRequestCode, reloadIntent, 0));
            }

            if (!componentInfo.isEnabled(mContext) || (component.isRequiresInternetAccess()
                    && !NetworkManager.isConnected(mRequiredNetwork))) {
                data.setLastRequestCode(componentInfo.getComponentClass(), -1);
                return;
            }

            int newRequestCode = NotificationIdsManager.getInstance().obtainRequestCode();
            data.setLastRequestCode(componentInfo.getComponentClass(), newRequestCode);
            TimedComponent.RepeatingMode repeatingMode = component.getRepeatingMode();
            Calendar calendar = Calendar.getInstance();
            int[] usableDays = component.getUsableDays();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int startHour = component.getStartHour();
            int stopHour = component.getStopHour();
            if ((startHour != stopHour && !(startHour < stopHour
                    ? (hour >= startHour && hour < stopHour)
                    : (hour >= startHour || hour < stopHour)))
                    || !Arrays.contains(usableDays, calendar.get(Calendar.DAY_OF_WEEK))) {
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 1);

                while (!Arrays.contains(usableDays, calendar.get(Calendar.DAY_OF_WEEK)))
                    calendar.add(Calendar.DAY_OF_WEEK, 1);

                while (!(startHour < stopHour ? (hour >= startHour && hour < stopHour)
                        : (hour >= startHour || hour < stopHour))) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                }

                alarms.set(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                        calendar.getTimeInMillis(), PendingIntent.getBroadcast(mContext,
                                newRequestCode, reloadIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                return;
            }

            long lastStart = data.getLastExecuteTime(componentInfo.getComponentClass());
            long startInterval = component.getRepeatTime();
            if (lastStart == -1L) lastStart = calendar.getTimeInMillis() - startInterval;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, newRequestCode,
                    componentIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (repeatingMode.inexact()) {
                alarms.setInexactRepeating(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                        lastStart + startInterval, startInterval, pendingIntent);
            } else {
                alarms.setRepeating(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                        lastStart + startInterval, startInterval, pendingIntent);
            }

            if (startHour != stopHour) {
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 5);

                hour = calendar.get(Calendar.HOUR_OF_DAY);
                while ((startHour < stopHour ? (hour >= startHour && hour < stopHour)
                        : (hour >= startHour || hour < stopHour)) && Arrays
                        .contains(usableDays, calendar.get(Calendar.DAY_OF_WEEK))) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                }

                alarms.set(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                        calendar.getTimeInMillis(), PendingIntent.getBroadcast(mContext,
                                newRequestCode, reloadIntent, PendingIntent.FLAG_CANCEL_CURRENT));
            }
        }
    }

    @Nullable
    public TimCompInfo getComponentInfo(Class<?> componentClass) {
        synchronized (mComponentsInfoMap) {
            return mComponentsInfoMap.get(componentClass);
        }
    }

    public void forceExecute(Class<?> componentClass) {
        forceExecute(componentClass, null);
    }

    public void forceExecute(Class<?> componentClass, @Nullable Bundle extras) {
        TimCompInfo componentInfo = getComponentInfo(componentClass);
        if (componentInfo == null)
            throw new NullPointerException(componentClass.getName() + " no found");
        forceExecute(componentInfo, extras);
    }

    public void forceExecute(@NonNull TimCompInfo componentInfo) {
        forceExecute(componentInfo, null);
    }

    public void forceExecute(@NonNull TimCompInfo componentInfo, @Nullable Bundle extras) {
        mContext.sendBroadcast(TimedComponentExecutor.generateIntent(mContext,
                ACTION_FORCED_EXECUTE, componentInfo, extras));
    }
}

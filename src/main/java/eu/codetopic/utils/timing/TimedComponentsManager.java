package eu.codetopic.utils.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.Objects;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.timing.info.TimCompInfo;
import eu.codetopic.utils.timing.info.TimCompInfoData;
import eu.codetopic.utils.timing.info.TimedComponent;

public class TimedComponentsManager {

    public static final String ACTION_TIMED_EXECUTE = "eu.codetopic.utils.timing.TimedComponentsManager.ACTION_TIMED_EXECUTE";
    public static final String ACTION_FORCED_EXECUTE = "eu.codetopic.utils.timing.TimedComponentsManager.ACTION_FORCED_EXECUTE";
    private static final String ACTION_RELOAD_COMPONENT = "eu.codetopic.utils.timing.TimedComponentsManager.RELOAD_COMPONENT";
    private static final String EXTRA_TIMED_COMPONENT_CLASS_NAME = "eu.codetopic.utils.timing.TimedComponentsManager.TIMED_COMPONENT_CLASS_NAME";

    private static final String LOG_TAG = "TimedComponentsManager";
    private static TimedComponentsManager INSTANCE = null;

    private final Context mContext;
    private final HashMap<Class<?>, TimCompInfo> mComponentsInfoMap;
    private NetworkManager.NetworkType mRequiredNetwork;

    private TimedComponentsManager(Context context, @NonNull NetworkManager.NetworkType requiredNetwork,
                                   Class<?>[] components) {

        mContext = context;
        mRequiredNetwork = requiredNetwork;

        mComponentsInfoMap = new HashMap<>(components.length);
        Log.d(LOG_TAG, "<init> initializing for: " + java.util.Arrays.toString(components));
        synchronized (mComponentsInfoMap) {
            for (Class<?> component : components) {
                try {
                    mComponentsInfoMap.put(component, TimCompInfo.createInfoFor(mContext, component));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "<init> - problem detected during initialisation of "
                            + component.getName(), t);
                }
            }
        }
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context         application context
     * @param timedComponents Classes of timed components to use in TimedComponentsManager
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
    public static void initialize(Context context, @NonNull NetworkManager.NetworkType requiredNetwork,
                                  Class<?>... timedComponents) {

        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        context = context.getApplicationContext();
        TimingData.initialize(context);

        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootConnectivityReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        INSTANCE = new TimedComponentsManager(context, requiredNetwork, timedComponents);

        if (TimingData.getter.get().isFirstLoad()) INSTANCE.reloadAll();
    }

    public static TimedComponentsManager getInstance() {
        return INSTANCE;
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    void proceedIntent(Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                TimingData data = TimingData.getter.get();
                synchronized (mComponentsInfoMap) {
                    for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                        if (componentInfo.getComponentProperties().isResetRepeatingOnBoot())
                            data.clear(componentInfo.getComponentClass());

                    reloadAll();
                }
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
            case ConnectivityManager.CONNECTIVITY_ACTION:
                reloadAllNetwork();
                break;
            case ACTION_RELOAD_COMPONENT:
                Class<?> clazz;
                try {
                    clazz = Class.forName(intent.getStringExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME));
                } catch (ClassNotFoundException e) {
                    Log.e(LOG_TAG, "proceedIntent: can't find requested class to reload", e);
                    break;
                }
                tryReload(clazz);
                break;
        }
    }

    public Context getContext() {
        return mContext;
    }

    @NonNull
    public NetworkManager.NetworkType getRequiredNetwork() {
        return mRequiredNetwork;
    }

    public void setRequiredNetwork(@NonNull NetworkManager.NetworkType requiredNetwork) {
        if (!Objects.equals(this.mRequiredNetwork, requiredNetwork)) {
            this.mRequiredNetwork = requiredNetwork;
            reloadAllNetwork();
        }
    }

    public void setComponentEnabled(@NonNull Class<?> componentClass, boolean enabled) {
        synchronized (mComponentsInfoMap) {
            setComponentEnabledInternal(getComponentInfoNonNull(componentClass), enabled);
        }
    }

    public void setComponentEnabled(@NonNull TimCompInfo componentInfo, boolean enabled) {
        synchronized (mComponentsInfoMap) {
            validateComponentInfo(componentInfo);
            setComponentEnabledInternal(componentInfo, enabled);
        }
    }

    private void setComponentEnabledInternal(@NonNull TimCompInfo componentInfo, boolean enabled) {
        synchronized (mComponentsInfoMap) {
            PackageManager pm = mContext.getPackageManager();
            if (pm.getComponentEnabledSetting(componentInfo.getComponentName(mContext))
                    != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                    && enabled == componentInfo.isEnabled(mContext)) return;

            pm.setComponentEnabledSetting(componentInfo.getComponentName(mContext),
                    enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            tryReloadInternal(componentInfo);
        }
    }

    public Intent getReloadIntent(@NonNull Class<?> componentClass) {
        return getReloadIntentInternal(getComponentInfoNonNull(componentClass));
    }

    public Intent getReloadIntent(@NonNull TimCompInfo componentInfo) {
        validateComponentInfo(componentInfo);
        return getReloadIntentInternal(componentInfo);
    }

    private Intent getReloadIntentInternal(@NonNull TimCompInfo componentInfo) {
        return new Intent(mContext, BootConnectivityReceiver.class)
                .setAction(ACTION_RELOAD_COMPONENT)
                .putExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME,// fixes api 24 class passing trough PendingIntent
                        componentInfo.getComponentClass().getName());
    }

    public void reloadAllNetwork() {
        synchronized (mComponentsInfoMap) {
            for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                if (componentInfo.getComponentProperties().isRequiresInternetAccess())
                    tryReloadInternal(componentInfo);
        }
    }

    public void reloadAll() {
        synchronized (mComponentsInfoMap) {
            for (TimCompInfo componentInfo : mComponentsInfoMap.values())
                tryReloadInternal(componentInfo);
        }
    }

    public void reloadComponentModifications(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMap) {
            reloadComponentModificationsInternal(getComponentInfoNonNull(componentClass));
        }
    }

    public void reloadComponentModifications(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMap) {
            validateComponentInfo(componentInfo);
            reloadComponentModificationsInternal(componentInfo);
        }
    }

    private void reloadComponentModificationsInternal(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMap) {
            componentInfo.getComponentProperties().reloadModifications(mContext);
            tryReloadInternal(componentInfo);
        }
    }

    public boolean tryReload(@NonNull Class<?> componentClass) {
        try {
            reload(componentClass);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryReload - problem detected while reloading timed component: "
                    + componentClass.getName(), e);
            return false;
        }
    }

    public boolean tryReload(@NonNull TimCompInfo componentInfo) {
        try {
            reload(componentInfo);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryReload - problem detected while reloading timed component: "
                    + componentInfo.getComponentClass().getName(), e);
            return false;
        }
    }

    private boolean tryReloadInternal(@NonNull TimCompInfo componentInfo) {
        try {
            reloadInternal(componentInfo);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "tryReload - problem detected while reloading timed component: "
                    + componentInfo.getComponentClass().getName(), e);
            return false;
        }
    }

    public void reload(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMap) {
            reloadInternal(getComponentInfoNonNull(componentClass));
        }
    }

    public void reload(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMap) {
            validateComponentInfo(componentInfo);
            reloadInternal(componentInfo);
        }
    }

    private void reloadInternal(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMap) {
            AlarmManager alarms = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent componentIntent = TimedComponentExecutor.generateIntent(mContext,
                    ACTION_TIMED_EXECUTE, componentInfo.getComponentClass(), null);
            Intent reloadIntent = getReloadIntentInternal(componentInfo);

            TimingData data = TimingData.getter.get();
            TimCompInfoData component = componentInfo.getComponentProperties();
            int lastRequestCode = data.getLastRequestCode(componentInfo.getComponentClass());
            if (lastRequestCode != -1) {
                PendingIntent oldComponentPendingIntent = PendingIntent.getBroadcast(mContext,
                        lastRequestCode, componentIntent, PendingIntent.FLAG_NO_CREATE);
                if (oldComponentPendingIntent != null) alarms.cancel(oldComponentPendingIntent);

                PendingIntent oldReloadPendingIntent = PendingIntent.getBroadcast(mContext,
                        lastRequestCode, reloadIntent, PendingIntent.FLAG_NO_CREATE);
                if (oldReloadPendingIntent != null) alarms.cancel(oldReloadPendingIntent);
            }

            if (!componentInfo.isEnabled(mContext) || (component.isRequiresInternetAccess()
                    && !NetworkManager.isConnected(mRequiredNetwork))) {
                data.setLastRequestCode(componentInfo.getComponentClass(), -1);
                return;
            }

            int newRequestCode = Identifiers.next(Identifiers.TYPE_REQUEST_CODE);
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

    public Collection<TimCompInfo> getAllTimedComponentInfo() {
        synchronized (mComponentsInfoMap) {
            return mComponentsInfoMap.values();
        }
    }

    @Nullable
    public TimCompInfo getComponentInfo(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMap) {
            return mComponentsInfoMap.get(componentClass);
        }
    }

    @NonNull
    public TimCompInfo getComponentInfoNonNull(@NonNull Class<?> componentClass) {
        synchronized (mComponentsInfoMap) {
            TimCompInfo componentInfo = mComponentsInfoMap.get(componentClass);
            if (componentInfo == null)
                throw new NullPointerException(componentClass.getName() + " no found");
            return componentInfo;
        }
    }

    public TimCompInfo validateComponentInfo(@NonNull TimCompInfo componentInfo) {
        synchronized (mComponentsInfoMap) {
            if (!mComponentsInfoMap.containsValue(componentInfo))
                throw new IllegalArgumentException("Can't reload componentInfo that is not initialized in "
                        + LOG_TAG + ".initialize(). componentInfo=" + componentInfo);
            return componentInfo;
        }
    }

    public void forceExecute(@NonNull Class<?> componentClass) {
        forceExecute(componentClass, null);
    }

    public void forceExecute(@NonNull Class<?> componentClass, @Nullable Bundle extras) {
        forceExecuteInternal(getComponentInfoNonNull(componentClass), extras);
    }

    public void forceExecute(@NonNull TimCompInfo componentInfo) {
        forceExecute(componentInfo, null);
    }

    public void forceExecute(@NonNull TimCompInfo componentInfo, @Nullable Bundle extras) {
        validateComponentInfo(componentInfo);
        forceExecuteInternal(componentInfo, extras);
    }

    private void forceExecuteInternal(@NonNull TimCompInfo componentInfo, @Nullable Bundle extras) {
        mContext.sendBroadcast(TimedComponentExecutor.generateIntent(mContext,
                ACTION_FORCED_EXECUTE, componentInfo.getComponentClass(), extras));
    }

    @Override
    public String toString() {
        return "TimedComponentsManager{" +
                "mComponentsInfoMap=" + mComponentsInfoMap +
                ", mRequiredNetwork=" + mRequiredNetwork +
                '}';
    }
}

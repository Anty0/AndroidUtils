package eu.codetopic.utils.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.exceptions.NoModuleFoundException;
import eu.codetopic.utils.notifications.manage.NotificationIdsManager;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public class TimedBroadcastsManager {

    public static final String ACTION_TIMED_EXECUTE = "eu.codetopic.utils.timing.TimedBroadcastsManager.ACTION_TIMED_EXECUTE";
    public static final String ACTION_FORCED_EXECUTE = "eu.codetopic.utils.timing.TimedBroadcastsManager.ACTION_FORCED_EXECUTE";
    private static final String ACTION_RELOAD_BROADCAST = "eu.codetopic.utils.timing.TimedBroadcastsManager.RELOAD_BROADCAST";
    private static final String EXTRA_TIMED_BROADCAST_INFO = "eu.codetopic.utils.timing.TimedBroadcastsManager.TIMED_BROADCAST_INFO";

    private static final String LOG_TAG = "TimedBroadcastsManager";
    private static TimedBroadcastsManager INSTANCE = null;

    private final Context mContext;
    private final NetworkManager.NetworkType mRequiredNetwork;
    private final DataGetter<TimingData> mTimingDataGetter;
    private final HashMap<Class<?>, TimedBroadcastInfo> mBroadcastsInfoMap;

    private TimedBroadcastsManager(Context context, NetworkManager.NetworkType requiredNetwork,
                                   @NonNull DataGetter<TimingData> timingDataGetter,
                                   Class<? extends BroadcastReceiver>[] broadcasts) {

        mContext = context;
        mRequiredNetwork = requiredNetwork;
        mTimingDataGetter = timingDataGetter;

        if (NotificationIdsManager.getInstance() == null)
            throw new NoModuleFoundException("NotificationIdsManager no found please add it to ModulesManager initialization");

        mBroadcastsInfoMap = new HashMap<>(broadcasts.length);
        Log.d(LOG_TAG, "<init> initialising for: " + java.util.Arrays.toString(broadcasts));
        synchronized (mBroadcastsInfoMap) {
            for (Class<?> broadcast : broadcasts)
                try {
                    mBroadcastsInfoMap.put(broadcast, new TimedBroadcastInfo(broadcast));
                } catch (Throwable t) {
                    Log.e(LOG_TAG, "<init>", t);
                }
        }
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context          application context
     * @param requiredNetwork  required network to execute timed broadcasts that requires internet access
     * @param timingDataGetter ModuleDataGetter of TimingData for saving required data using SharedPreferences
     * @param timedBroadcasts  Classes of timed broadcasts to use in TimedBroadcastsManager
     */
    @SafeVarargs
    public static void initialize(Context context, NetworkManager.NetworkType requiredNetwork,
                                  @NonNull DataGetter<TimingData> timingDataGetter,
                                  Class<? extends BroadcastReceiver>... timedBroadcasts) {// TODO: 26.3.16 initialize in ApplicationBase
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, BootConnectivityReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        /*List<Class<?>> broadcasts = CPScanner.scanClasses(new ClassFilter()
                .superClass(BroadcastReceiver.class).annotation(TimedBroadcast.class));*/

        INSTANCE = new TimedBroadcastsManager(context, requiredNetwork,
                timingDataGetter, timedBroadcasts);
    }

    public static TimedBroadcastsManager getInstance() {
        return INSTANCE;
    }

    public static boolean isInitialized() {
        return INSTANCE != null;
    }

    void notifyIntentReceived(Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                TimingData data = mTimingDataGetter.get();
                synchronized (mBroadcastsInfoMap) {
                    for (TimedBroadcastInfo broadcastInfo : mBroadcastsInfoMap.values())
                        if (broadcastInfo.getBroadcastInfo().resetTimingOnBoot())
                            data.clear(broadcastInfo.getBroadcastClass());
                    reloadAll();
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                synchronized (mBroadcastsInfoMap) {
                    for (TimedBroadcastInfo broadcastInfo : mBroadcastsInfoMap.values())
                        if (broadcastInfo.getBroadcastInfo().requiresInternetAccess())
                            try {
                                reload(broadcastInfo);
                            } catch (Exception e) {
                                Log.e(LOG_TAG, "reloadAll - problem detected while reloading timed broadcast: "
                                        + broadcastInfo.getBroadcastClass().getName(), e);
                            }
                }
                break;
            case ACTION_RELOAD_BROADCAST:
                TimedBroadcastInfo broadcastInfo = (TimedBroadcastInfo) intent
                        .getSerializableExtra(EXTRA_TIMED_BROADCAST_INFO);
                try {
                    reload(broadcastInfo);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "reloadAll - problem detected while reloading timed broadcast: "
                            + broadcastInfo.getBroadcastClass().getName(), e);
                }
                break;
        }
    }

    public void setBroadcastEnabled(Class<?> broadcastClass, boolean enabled) {
        TimedBroadcastInfo broadcastInfo = getBroadcastInfo(broadcastClass);
        if (broadcastInfo == null)
            throw new NullPointerException(broadcastClass.getName() + " no found");
        setBroadcastEnabled(broadcastInfo, enabled);
    }

    public void setBroadcastEnabled(TimedBroadcastInfo broadcastInfo, boolean enabled) {
        mContext.getPackageManager().setComponentEnabledSetting(
                new ComponentName(mContext, broadcastInfo.getBroadcastClass()),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        reload(broadcastInfo);
    }

    public Intent getReloadIntent(Class<?> broadcastClass) {
        TimedBroadcastInfo broadcastInfo = getBroadcastInfo(broadcastClass);
        if (broadcastInfo == null)
            throw new NullPointerException(broadcastClass.getName() + " no found");
        return getReloadIntent(broadcastInfo);
    }

    public Intent getReloadIntent(TimedBroadcastInfo broadcastInfo) {
        return new Intent(ACTION_RELOAD_BROADCAST)
                .putExtra(EXTRA_TIMED_BROADCAST_INFO, broadcastInfo);
    }

    public void reloadAll() {
        synchronized (mBroadcastsInfoMap) {
            for (TimedBroadcastInfo broadcastInfo : mBroadcastsInfoMap.values())
                try {
                    reload(broadcastInfo);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "reloadAll - problem detected while reloading timed broadcast: "
                            + broadcastInfo.getBroadcastClass().getName(), e);
                }
        }
    }

    public void reload(Class<?> broadcastClass) {
        TimedBroadcastInfo broadcastInfo = getBroadcastInfo(broadcastClass);
        if (broadcastInfo == null)
            throw new NullPointerException(broadcastClass.getName() + " no found");
        reload(broadcastInfo);
    }

    public void reload(TimedBroadcastInfo broadcastInfo) {
        AlarmManager alarms = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent broadcastIntent = TimedBroadcastsExecutor.generateIntent(mContext,
                ACTION_TIMED_EXECUTE, mTimingDataGetter, broadcastInfo, null);
        Intent reloadIntent = getReloadIntent(broadcastInfo);

        TimingData data = mTimingDataGetter.get();
        TimedBroadcast broadcast = broadcastInfo.getBroadcastInfo();
        int lastRequestCode = data.getLastRequestCode(broadcastInfo.getBroadcastClass());
        if (lastRequestCode != -1) {
            alarms.cancel(PendingIntent.getBroadcast(mContext, lastRequestCode, broadcastIntent, 0));
            alarms.cancel(PendingIntent.getBroadcast(mContext, lastRequestCode, reloadIntent, 0));
        }

        int enabledState = broadcastInfo.getComponentEnabledState(mContext);
        if (enabledState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                (!broadcast.defaultEnabledState()
                        && enabledState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
                || (broadcast.requiresInternetAccess()
                && !NetworkManager.isConnected(mRequiredNetwork))) {
            data.setLastRequestCode(broadcastInfo.getBroadcastClass(), -1);
            return;
        }

        int newRequestCode = NotificationIdsManager.getInstance().obtainRequestCode();
        data.setLastRequestCode(broadcastInfo.getBroadcastClass(), newRequestCode);
        TimedBroadcast.RepeatingMode repeatingMode = broadcast.repeatingMode();
        Calendar calendar = Calendar.getInstance();
        int[] usableDays = broadcast.usableDays();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int startHour = broadcast.startHour();
        int stopHour = broadcast.stopHour();
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

        long lastStart = data.getLastExecuteTime(broadcastInfo.getBroadcastClass());
        long startInterval = broadcast.time();
        if (lastStart == -1L) lastStart = calendar.getTimeInMillis() - startInterval;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, newRequestCode,
                broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
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

    @Nullable
    public TimedBroadcastInfo getBroadcastInfo(Class<?> broadcastClass) {
        synchronized (mBroadcastsInfoMap) {
            return mBroadcastsInfoMap.get(broadcastClass);
        }
    }

    public void forceExecute(Class<?> broadcastClass) {
        forceExecute(broadcastClass, null);
    }

    public void forceExecute(Class<?> broadcastClass, @Nullable Bundle extras) {
        TimedBroadcastInfo broadcastInfo = getBroadcastInfo(broadcastClass);
        if (broadcastInfo == null)
            throw new NullPointerException(broadcastClass.getName() + " no found");
        forceExecute(broadcastInfo, extras);
    }

    public void forceExecute(@NonNull TimedBroadcastInfo broadcastInfo) {
        forceExecute(broadcastInfo, null);
    }

    public void forceExecute(@NonNull TimedBroadcastInfo broadcastInfo, @Nullable Bundle extras) {
        mContext.sendBroadcast(TimedBroadcastsExecutor.generateIntent(mContext,
                ACTION_FORCED_EXECUTE, mTimingDataGetter, broadcastInfo, extras));
    }
}

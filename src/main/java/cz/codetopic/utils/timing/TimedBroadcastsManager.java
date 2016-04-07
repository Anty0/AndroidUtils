package cz.codetopic.utils.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import cz.codetopic.utils.Connectivity;
import cz.codetopic.utils.exceptions.NoModuleFoundException;
import cz.codetopic.utils.module.data.ModuleDataGetter;
import cz.codetopic.utils.notifications.manage.NotificationIdsModule;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public class TimedBroadcastsManager {

    public static final String ACTION_TIMED_EXECUTE = "cz.codetopic.utils.timing.TimedBroadcastsManager.ACTION_TIMED_EXECUTE";
    public static final String ACTION_FORCED_EXECUTE = "cz.codetopic.utils.timing.TimedBroadcastsManager.ACTION_FORCED_EXECUTE";
    private static final String LOG_TAG = "TimedBroadcastsManager";
    private static TimedBroadcastsManager INSTANCE = null;

    private final Context mContext;
    private final boolean mUseMobileNetworks;
    private final ModuleDataGetter<?, TimingData> mTimingDataGetter;
    private final HashMap<Class, TimedBroadcastInfo> mBroadcastsInfos;

    private TimedBroadcastsManager(Context context, boolean useMobileNetworks, @NonNull ModuleDataGetter
            <?, TimingData> timingDataGetter, Class... timedBroadcastsClasses) {
        mContext = context;
        mUseMobileNetworks = useMobileNetworks;
        mTimingDataGetter = timingDataGetter;

        if (NotificationIdsModule.getInstance() == null)
            throw new NoModuleFoundException("NotificationIdsModule no found please add it to ModulesManager initialization");

        mBroadcastsInfos = new HashMap<>(timedBroadcastsClasses.length);
        for (Class broadcast : timedBroadcastsClasses) {
            mBroadcastsInfos.put(broadcast, new TimedBroadcastInfo(broadcast));
        }
    }

    /**
     * This method must be called in onCreate() in Application, otherwise it won't work!
     *
     * @param context                application context
     * @param useMobileNetworks use mobile network if timed broadcast requires internet access
     * @param timingDataGetter ModuleDataGetter of TimingData for saving required data using SharedPreferences
     * @param timedBroadcastsClasses all broadcasts that uses TimedBroadcastsManager annotation
     */
    public static void initialize(Context context, boolean useMobileNetworks, @NonNull ModuleDataGetter
            <?, TimingData> timingDataGetter, Class... timedBroadcastsClasses) {// TODO: 26.3.16 initialize in ApplicationBase
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        INSTANCE = new TimedBroadcastsManager(context, useMobileNetworks,
                timingDataGetter, timedBroadcastsClasses);
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
                mTimingDataGetter.get().clear();
                reloadAll();
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                for (TimedBroadcastInfo broadcastInfo : mBroadcastsInfos.values())
                    if (broadcastInfo.getBroadcastInfo().requiresInternetAccess())
                        reload(broadcastInfo);
                break;
        }
    }

    public void reloadAll() {
        for (TimedBroadcastInfo broadcastInfo : mBroadcastsInfos.values())
            reload(broadcastInfo);
    }

    public void reload(Class broadcastClass) {
        TimedBroadcastInfo broadcastInfo = getBroadcastInfo(broadcastClass);
        if (broadcastInfo == null)
            throw new NullPointerException(broadcastClass.getName() + " no found");
        reload(broadcastInfo);
    }

    public void reload(TimedBroadcastInfo broadcastInfo) {
        AlarmManager alarms = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = TimedBroadcastsExecutor.generateIntent(mContext,
                ACTION_TIMED_EXECUTE, mTimingDataGetter, broadcastInfo, null);

        TimingData data = mTimingDataGetter.get();
        int lastRequestCode = data.getLastRequestCode(broadcastInfo.getBroadcastClass());
        if (lastRequestCode != -1)
            alarms.cancel(PendingIntent.getBroadcast(mContext, lastRequestCode, intent, 0));

        int enabledState = broadcastInfo.getComponentEnabledState(mContext);
        if (enabledState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                (!broadcastInfo.getBroadcastInfo().defaultEnabledState()
                        && enabledState == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
                || (broadcastInfo.getBroadcastInfo().requiresInternetAccess()
                && !Connectivity.isNetworkAvailable(mContext, mUseMobileNetworks))) {
            data.setLastRequestCode(broadcastInfo.getBroadcastClass(), -1);
            return;
        }

        long lastStart = data.getLastExecuteTime(broadcastInfo.getBroadcastClass());
        long startInterval = broadcastInfo.getBroadcastInfo().time();
        int newRequestCode = NotificationIdsModule.getInstance().obtainRequestCode();
        data.setLastRequestCode(broadcastInfo.getBroadcastClass(), newRequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, newRequestCode,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        TimedBroadcast.RepeatingMode repeatingMode = broadcastInfo.getBroadcastInfo().repeatingMode();
        if (repeatingMode.inexact()) {
            alarms.setInexactRepeating(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                    lastStart + startInterval, startInterval, pendingIntent);
        } else {
            alarms.setRepeating(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                    lastStart + startInterval, startInterval, pendingIntent);
        }
    }

    @Nullable
    public TimedBroadcastInfo getBroadcastInfo(Class broadcastClass) {
        return mBroadcastsInfos.get(broadcastClass);
    }

    public void forceExecute(Class broadcastClass) {
        forceExecute(broadcastClass, null);
    }

    public void forceExecute(Class broadcastClass, @Nullable Bundle extras) {
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

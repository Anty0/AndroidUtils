package eu.codetopic.utils.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Calendar;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.timing.info.TimCompInfo;
import eu.codetopic.utils.timing.info.TimCompInfoData;
import eu.codetopic.utils.timing.info.TimedComponent;

import static eu.codetopic.utils.timing.TimedComponentsManager.ACTION_TIMED_EXECUTE;
import static eu.codetopic.utils.timing.TimedComponentsManager.getReloadComponentIntentInternal;

final class ComponentLoader {

    private static final String LOG_TAG = "ComponentLoader";
    private final Context context;
    private final NetworkManager.NetworkType requiredNetwork;
    @NonNull private final TimCompInfo componentInfo;

    private final AlarmManager alarmManager;
    private final Intent componentIntent;
    private final Intent reloadIntent;
    private final TimingData data = TimingData.getter.get();
    private final TimCompInfoData properties;

    ComponentLoader(Context context, NetworkManager.NetworkType requiredNetwork,
                    @NonNull TimCompInfo componentInfo) {

        this.context = context;
        this.requiredNetwork = requiredNetwork;
        this.componentInfo = componentInfo;

        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.componentIntent = TimedComponentExecutor.generateIntent(context,
                ACTION_TIMED_EXECUTE, componentInfo.getComponentClass(), null);
        this.reloadIntent = getReloadComponentIntentInternal(context, componentInfo);
        this.properties = componentInfo.getComponentProperties();
    }

    private void cancelOld() {
        int lastRequestCode = data.getLastRequestCode(componentInfo.getComponentClass());
        if (lastRequestCode != -1) {
            PendingIntent oldComponentPendingIntent = PendingIntent.getBroadcast(context,
                    lastRequestCode, componentIntent, PendingIntent.FLAG_NO_CREATE);
            if (oldComponentPendingIntent != null) alarmManager.cancel(oldComponentPendingIntent);

            PendingIntent oldReloadPendingIntent = PendingIntent.getBroadcast(context,
                    lastRequestCode, reloadIntent, PendingIntent.FLAG_NO_CREATE);
            if (oldReloadPendingIntent != null) alarmManager.cancel(oldReloadPendingIntent);
        }
    }

    public void reload() {
        cancelOld();

        if (!componentInfo.isEnabled(context) || (properties.isRequiresInternetAccess()
                && !NetworkManager.isConnected(requiredNetwork))) {
            data.setLastRequestCode(componentInfo.getComponentClass(), -1);
            return;
        }

        int newRequestCode = Identifiers.next(Identifiers.TYPE_REQUEST_CODE);
        data.setLastRequestCode(componentInfo.getComponentClass(), newRequestCode);
        TimedComponent.RepeatingMode repeatingMode = properties.getRepeatingMode();
        Calendar calendar = Calendar.getInstance();
        int[] usableDays = properties.getUsableDays();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int startHour = properties.getStartHour();
        int stopHour = properties.getStopHour();
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

            alarmManager.set(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                    calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,
                            newRequestCode, reloadIntent, PendingIntent.FLAG_CANCEL_CURRENT));
            return;
        }

        long lastStart = data.getLastExecuteTime(componentInfo.getComponentClass());
        long startInterval = properties.getRepeatTime();
        if (lastStart == -1L) lastStart = calendar.getTimeInMillis() - startInterval;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newRequestCode,
                componentIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (repeatingMode.inexact()) {
            alarmManager.setInexactRepeating(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                    lastStart + startInterval, startInterval, pendingIntent);
        } else {
            alarmManager.setRepeating(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
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

            alarmManager.set(repeatingMode.wakeUp() ? AlarmManager.RTC_WAKEUP : AlarmManager.RTC,
                    calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,
                            newRequestCode, reloadIntent, PendingIntent.FLAG_CANCEL_CURRENT));
        }
    }
}

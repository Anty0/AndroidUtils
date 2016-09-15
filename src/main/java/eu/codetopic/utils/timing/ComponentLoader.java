package eu.codetopic.utils.timing;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.util.Calendar;

import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.timing.info.TimCompInfo;
import eu.codetopic.utils.timing.info.TimCompInfoData;

import static eu.codetopic.utils.timing.TimedComponentsManager.ACTION_TIMED_EXECUTE;
import static eu.codetopic.utils.timing.TimedComponentsManager.getReloadComponentIntentInternal;

@MainThread
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

    ComponentLoader(@NonNull Class<?> componentClass) {
        this(TimedComponentsManager.getInstance().getComponentInfoNonNull(componentClass));
    }

    ComponentLoader(@NonNull TimCompInfo componentInfo) {

        TimedComponentsManager timCompMan = TimedComponentsManager.getInstance();
        this.context = timCompMan.getContext();
        this.requiredNetwork = timCompMan.getRequiredNetwork();
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
            if (oldComponentPendingIntent != null) {
                alarmManager.cancel(oldComponentPendingIntent);
            }

            PendingIntent oldReloadPendingIntent = PendingIntent.getBroadcast(context,
                    lastRequestCode, reloadIntent, PendingIntent.FLAG_NO_CREATE);
            if (oldReloadPendingIntent != null) {
                alarmManager.cancel(oldReloadPendingIntent);
            }
        }
    }

    private boolean checkIsReady() {
        if (!componentInfo.isReady(context, requiredNetwork)) {
            data.setLastRequestCode(componentInfo.getComponentClass(), -1);
            return true;
        }
        return false;
    }

    private boolean checkTimeRestrictions(int freeRequestCode) {
        if (!properties.isCurrentTimeInTimeRange()) {// TODO: 14.9.16 calculate exact next start time (don't use while)
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 1);

            while (!properties.isInUsableDaysRange(calendar.get(Calendar.DAY_OF_WEEK))) {
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            }

            while (!properties.isInHoursRange(calendar.get(Calendar.HOUR_OF_DAY))) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    PendingIntent.getBroadcast(context, freeRequestCode, reloadIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT));
            return true;
        }
        return false;
    }

    private void setupRepeating(int freeRequestCode) {
        long lastStart = data.getLastExecuteTime(componentInfo.getComponentClass());
        long startInterval = properties.getRepeatTime();
        long minimumLastStart = System.currentTimeMillis() - startInterval;
        if (lastStart < minimumLastStart) lastStart = minimumLastStart;
        alarmManager.setInexactRepeating(properties.isWakeUpForExecute() ? AlarmManager.RTC_WAKEUP
                        : AlarmManager.RTC, lastStart + startInterval, startInterval,
                PendingIntent.getBroadcast(context, freeRequestCode,
                        componentIntent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

    private boolean setupStoppingBasedOnTimeRestrictions(int freeRequestCode) {
        if (properties.hasTimeRestrictions()) {// TODO: 14.9.16 calculate exact next stop time (don't use while)
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 5);

            while (properties.isInTimeRange(calendar)) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    PendingIntent.getBroadcast(context, freeRequestCode, reloadIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT));
            return true;
        }
        return false;
    }

    public void reload() {
        cancelOld();

        if (checkIsReady()) return;

        int newRequestCode = Identifiers.next(Identifiers.TYPE_REQUEST_CODE);
        data.setLastRequestCode(componentInfo.getComponentClass(), newRequestCode);

        if (checkTimeRestrictions(newRequestCode)) return;
        setupRepeating(newRequestCode);
        setupStoppingBasedOnTimeRestrictions(newRequestCode);
    }
}

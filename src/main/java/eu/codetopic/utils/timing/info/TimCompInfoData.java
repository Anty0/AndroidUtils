package eu.codetopic.utils.timing.info;

import android.content.Context;
import android.support.annotation.MainThread;

import java.util.Calendar;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.exceptions.NoAnnotationPresentException;
import eu.codetopic.utils.log.Log;

@MainThread
public final class TimCompInfoData {

    private static final String LOG_TAG = "TimCompInfoData";

    private final Class<?> componentClass;
    private final TimCompInfoModifier[] modifiers;

    private long repeatTime;
    private int[] usableDays;
    private int startHour, stopHour;
    private boolean resetOnBoot, requiresInternetAccess;
    private boolean wakeUp;

    TimCompInfoData(Context context, Class<?> componentClass) {
        this.componentClass = componentClass;

        TimedComponent info = componentClass.getAnnotation(TimedComponent.class);
        if (info == null) throw new NoAnnotationPresentException(
                "TimedComponent annotation is not present in " + componentClass.getName());

        repeatTime = info.repeatTime();
        usableDays = info.usableDays();
        startHour = info.startHour();
        stopHour = info.stopHour();
        resetOnBoot = info.resetRepeatingOnBoot();
        requiresInternetAccess = info.requiresInternetAccess();
        wakeUp = info.wakeUpForExecute();

        Class<? extends TimCompInfoModifier>[] modifiersClasses = info.infoModifiers();
        TimCompInfoModifier[] modifiers = new TimCompInfoModifier[modifiersClasses.length];
        for (int i = 0, modifiersLength = modifiers.length; i < modifiersLength; i++) {
            try {
                modifiers[i] = modifiersClasses[i].newInstance();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Wrong modifier provided for " + componentClass.getName(), e);
            }
        }
        this.modifiers = Arrays.removeNulls(modifiers);
        reloadModifications(context);
    }

    public void reloadModifications(Context context) {
        synchronized (modifiers) {
            for (TimCompInfoModifier modifier : modifiers)
                modifier.modify(context, this);
        }
    }

    public void setResetOnBoot(boolean resetOnBoot) {
        this.resetOnBoot = resetOnBoot;
    }

    public long getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(long repeatTime) {
        this.repeatTime = repeatTime;
    }

    public int[] getUsableDays() {
        return usableDays;
    }

    public void setUsableDays(int[] usableDays) {
        this.usableDays = usableDays;
    }

    public boolean isInUsableDaysRange(int dayOfWeek) {
        return Arrays.contains(usableDays, dayOfWeek);
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStopHour() {
        return stopHour;
    }

    public void setStopHour(int stopHour) {
        this.stopHour = stopHour;
    }

    public boolean isInHoursRange(int hour) {
        return !hasTimeRestrictions() || (startHour < stopHour
                ? (hour >= startHour && hour < stopHour)
                : (hour >= startHour || hour < stopHour));
    }

    public boolean isCurrentTimeInTimeRange() {
        return isInTimeRange(Calendar.getInstance());
    }

    public boolean isInTimeRange(long timeInMilis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilis);
        return isInTimeRange(calendar);
    }

    public boolean isInTimeRange(Calendar calendar) {
        return isInHoursRange(calendar.get(Calendar.HOUR_OF_DAY)) &&
                isInUsableDaysRange(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public boolean hasTimeRestrictions() {
        return startHour != stopHour;
    }

    public boolean isResetRepeatingOnBoot() {
        return resetOnBoot;
    }

    public boolean isRequiresInternetAccess() {
        return requiresInternetAccess;
    }

    public void setRequiresInternetAccess(boolean requiresInternetAccess) {
        this.requiresInternetAccess = requiresInternetAccess;
    }

    public boolean isWakeUpForExecute() {
        return wakeUp;
    }

    public void setWakeUpForExecute(boolean wakeUp) {
        this.wakeUp = wakeUp;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public TimCompInfoModifier[] getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "TimCompInfoData{" +
                "componentClass=" + componentClass +
                ", modifiers=" + java.util.Arrays.toString(modifiers) +
                ", repeatTime=" + repeatTime +
                ", usableDays=" + java.util.Arrays.toString(usableDays) +
                ", startHour=" + startHour +
                ", stopHour=" + stopHour +
                ", resetOnBoot=" + resetOnBoot +
                ", requiresInternetAccess=" + requiresInternetAccess +
                ", wakeUp=" + wakeUp +
                '}';
    }
}

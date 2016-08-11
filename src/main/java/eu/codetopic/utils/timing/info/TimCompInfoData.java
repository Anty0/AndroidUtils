package eu.codetopic.utils.timing.info;

import java.io.Serializable;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.exceptions.NoAnnotationPresentException;

public class TimCompInfoData implements Serializable {

    private static final String LOG_TAG = "TimCompInfoData";

    private final Class<?> componentClass;
    private final TimCompInfoModifier[] modifiers;

    private long repeatTime;
    private int[] usableDays;
    private int startHour, stopHour;
    private boolean resetOnBoot, requiresInternetAccess;
    private TimedComponent.RepeatingMode repeatingMode;

    TimCompInfoData(Class<?> componentClass) {
        this.componentClass = componentClass;

        TimedComponent info = componentClass.getAnnotation(TimedComponent.class);
        if (info == null)
            throw new NoAnnotationPresentException("TimedComponent annotation is not present in "
                    + componentClass.getName());

        repeatTime = info.repeatTime();
        usableDays = info.usableDays();
        startHour = info.startHour();
        stopHour = info.stopHour();
        resetOnBoot = info.resetRepeatingOnBoot();
        requiresInternetAccess = info.requiresInternetAccess();
        repeatingMode = info.repeatingMode();

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
        reloadModifications();
    }

    public void reloadModifications() {
        synchronized (modifiers) {
            for (TimCompInfoModifier modifier : modifiers)
                modifier.modify(this);
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

    public boolean isResetRepeatingOnBoot() {
        return resetOnBoot;
    }

    public boolean isRequiresInternetAccess() {
        return requiresInternetAccess;
    }

    public void setRequiresInternetAccess(boolean requiresInternetAccess) {
        this.requiresInternetAccess = requiresInternetAccess;
    }

    public TimedComponent.RepeatingMode getRepeatingMode() {
        return repeatingMode;
    }

    public void setRepeatingMode(TimedComponent.RepeatingMode repeatingMode) {
        this.repeatingMode = repeatingMode;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

    public TimCompInfoModifier[] getModifiers() {
        return modifiers;
    }
}

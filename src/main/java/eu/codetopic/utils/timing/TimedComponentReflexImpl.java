package eu.codetopic.utils.timing;

import java.lang.annotation.Annotation;
import java.util.Calendar;

@SuppressWarnings("ClassExplicitlyAnnotation")
public abstract class TimedComponentReflexImpl implements TimedComponent {

    @Override
    public int[] usableDays() {
        return new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    }

    @Override
    public int startHour() {
        return 0;
    }

    @Override
    public int stopHour() {
        return 0;
    }

    @Override
    public boolean resetTimingOnBoot() {
        return false;
    }

    @Override
    public boolean requiresInternetAccess() {
        return false;
    }

    @Override
    public RepeatingMode repeatingMode() {
        return RepeatingMode.INEXACT_REPEATING_WAKE_UP;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TimedComponent.class;
    }
}

package com.codetopic.utils.timing.info;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Calendar;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMemberNames;
import proguard.annotation.KeepClassMembers;
import proguard.annotation.KeepName;

/**
 * Annotation for components that uses any repeating or timed starting.
 * Class annotated using this annotation must be registered in TimingComponentsManager.
 *
 * @author anty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Keep
@KeepName
public @interface TimedComponent {

    long repeatTime();

    int[] usableDays() default {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};

    int startHour() default 0;

    int stopHour() default 0;

    boolean resetRepeatingOnBoot() default false;

    boolean requiresInternetAccess() default false;

    RepeatingMode repeatingMode() default RepeatingMode.INEXACT_REPEATING_WAKE_UP;

    Class<? extends TimCompInfoModifier>[] infoModifiers() default {};

    @Keep
    @KeepName
    @KeepClassMembers
    @KeepClassMemberNames
    enum RepeatingMode {
        INEXACT_REPEATING_WAKE_UP, INEXACT_REPEATING, REPEATING_WAKE_UP, REPEATING;

        public boolean inexact() {
            switch (this) {
                case INEXACT_REPEATING:
                case INEXACT_REPEATING_WAKE_UP:
                    return true;
                case REPEATING:
                case REPEATING_WAKE_UP:
                default:
                    return false;
            }
        }

        public boolean wakeUp() {
            switch (this) {
                case INEXACT_REPEATING_WAKE_UP:
                case REPEATING_WAKE_UP:
                    return true;
                case INEXACT_REPEATING:
                case REPEATING:
                default:
                    return false;
            }
        }
    }
}

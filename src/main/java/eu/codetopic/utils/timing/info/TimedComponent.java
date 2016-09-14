package eu.codetopic.utils.timing.info;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Calendar;

import proguard.annotation.Keep;
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

    boolean wakeUpForExecute() default true;

    Class<? extends TimCompInfoModifier>[] infoModifiers() default {};
}

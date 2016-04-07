package cz.codetopic.utils.timing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMemberNames;
import proguard.annotation.KeepClassMembers;
import proguard.annotation.KeepName;

/**
 * Annotation for Broadcasts that using any repeating or timed starting.
 * Class annotated using this annotation must be registered in TimingBroadcastsManager
 * and must have KeepName annotation.
 *
 * @author anty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Keep
@KeepName
public @interface TimedBroadcast {

    long time();

    boolean resetTimingOnBoot() default false;

    boolean requiresInternetAccess() default false;

    RepeatingMode repeatingMode() default RepeatingMode.INEXACT_REPEATING_WAKE_UP;

    boolean defaultEnabledState() default true;

    @Keep
    @KeepName
    @KeepClassMembers
    @KeepClassMemberNames
    enum RepeatingMode {
        INEXACT_REPEATING_WAKE_UP, INEXACT_REPEATING, REPEATING_WAKE_UP, REPEATING;

        boolean inexact() {
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

        boolean wakeUp() {
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

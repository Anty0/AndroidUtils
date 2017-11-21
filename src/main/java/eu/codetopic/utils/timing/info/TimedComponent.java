/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

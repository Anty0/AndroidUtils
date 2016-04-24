package eu.codetopic.utils.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Created by anty on 24.4.16.
 *
 * @author anty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Keep
@KeepName
public @interface DisableModule {}

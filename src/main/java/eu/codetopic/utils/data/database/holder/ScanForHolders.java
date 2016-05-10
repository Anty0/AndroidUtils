package eu.codetopic.utils.data.database.holder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Created by anty on 5.4.16.
 *
 * @author anty
 */
@Keep
@KeepName
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ScanForHolders {
}

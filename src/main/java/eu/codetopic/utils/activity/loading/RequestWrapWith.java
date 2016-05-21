package eu.codetopic.utils.activity.loading;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Created by anty on 20.5.16.
 *
 * @author anty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Keep
@KeepName
public @interface RequestWrapWith {

    @LayoutRes int wrappingLayoutRes();

    @IdRes int contentLayoutId();
}

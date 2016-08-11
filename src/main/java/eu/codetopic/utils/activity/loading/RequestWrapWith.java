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
 * Migrate to eu.codetopic.utils.view.holder.loading.* instead
 */
@Deprecated
@SuppressWarnings("deprecation")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Keep
@KeepName
public @interface RequestWrapWith {

    @LayoutRes int wrappingLayoutRes();

    @IdRes int contentLayoutId();
}

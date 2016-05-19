package eu.codetopic.utils.thread.service;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.codetopic.utils.thread.ProgressReporter;
import proguard.annotation.Keep;
import proguard.annotation.KeepName;

public interface ServiceWork {

    Params getJobParams(Context context);

    NotificationCompat.Extender getNotificationExtender(Context context);

    void run(Context context, ProgressReporter reporter) throws Throwable;

    @Keep
    @KeepName
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RetryLimit {
        int value() default Job.DEFAULT_RETRY_LIMIT;
    }

    @Keep
    @KeepName
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UseProgress {
        boolean value() default true;
    }

}

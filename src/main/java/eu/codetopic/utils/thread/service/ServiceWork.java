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

package eu.codetopic.utils.thread.service;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.codetopic.utils.thread.progress.ProgressReporter;
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

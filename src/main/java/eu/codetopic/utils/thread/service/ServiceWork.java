/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

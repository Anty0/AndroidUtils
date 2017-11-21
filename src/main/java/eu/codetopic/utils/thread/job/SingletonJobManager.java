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

package eu.codetopic.utils.thread.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import eu.codetopic.utils.data.getter.JobManagerGetter;

public final class SingletonJobManager {

    public static final JobManagerGetter getter = new SingletonJobManagerGetter();
    private static final String LOG_TAG = "SingletonJobManager";

    private static JobManager mInstance = null;

    private SingletonJobManager() {
    }

    public static void initialize(@NonNull Context context) {
        initialize(new JobManager(new Configuration.Builder(context).id(LOG_TAG).build()));// TODO: 14.10.16 add support for per process job manager
    }

    public static void initialize(@NonNull JobManager jobManagerInstance) {
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = jobManagerInstance;
    }

    public static boolean isInitialized() {
        return mInstance != null;
    }

    public static JobManager getInstance() {
        return mInstance;
    }

    /**
     * Use constant SingletonJobManager.getter instead
     *
     * @return getter for SingletonJobManager
     */
    @Deprecated
    public static JobManagerGetter getGetter() {
        return getter;
    }

}

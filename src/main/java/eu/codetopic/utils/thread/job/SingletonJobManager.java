/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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

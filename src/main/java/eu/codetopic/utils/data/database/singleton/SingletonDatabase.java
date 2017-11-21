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

package eu.codetopic.utils.data.database.singleton;

import android.support.annotation.NonNull;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.SingletonJobManager;

public final class SingletonDatabase {

    private static final String LOG_TAG = "SingletonDatabase";

    private static DatabaseBase mInstance = null;
    private static boolean mDatabaseReady = false;

    private SingletonDatabase() {
    }

    public static void initialize(@NonNull DatabaseBase database) {
        if (!SingletonJobManager.isInitialized())
            throw new IllegalStateException("SingletonJobManager must be initialized before " + LOG_TAG);
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = database;

        SingletonJobManager.getInstance().stop();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mInstance.initDaos();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "initialize", e);
                    // FIXME: 27.5.16 find way to report this error to application to solve this problem
                } finally {
                    SingletonJobManager.getInstance().start();
                    mDatabaseReady = true;
                }
            }
        }).start();
    }

    public static boolean isInitialized() {
        return mInstance != null;
    }

    public static boolean isDatabaseReady() {
        return mDatabaseReady;
    }

    public static DatabaseBase getInstance() {
        return mInstance;
    }

    public static <DT, ID> DatabaseDaoGetter<DT, ID> getGetterFor(Class<DT> dataClass) {
        return new SingletonDatabaseDaoGetter<>(dataClass);
    }
}

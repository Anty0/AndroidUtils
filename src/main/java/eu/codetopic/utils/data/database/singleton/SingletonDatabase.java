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

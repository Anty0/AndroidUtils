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

package eu.codetopic.utils.log;

import android.content.Context;
import android.support.annotation.MainThread;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.java.utils.log.Logger;
import eu.codetopic.java.utils.log.LogsHandler;
import eu.codetopic.java.utils.log.base.LogLine;
import eu.codetopic.java.utils.log.base.Priority;
import eu.codetopic.utils.BuildConfig;
import eu.codetopic.utils.thread.JobUtils;

public final class AndroidLoggerExtension {

    private static final String LOG_TAG = "AndroidLoggerExtension";

    private static boolean INSTALLED = false;

    private AndroidLoggerExtension() {
    }

    @MainThread
    public static synchronized void install(Context context) {
        if (INSTALLED) throw new IllegalStateException(LOG_TAG + " is still installed.");
        INSTALLED = true;

        Log.setDebugMode(BuildConfig.DEBUG);
        Logger.setLogTarget(new AndroidLogTarget());

        final Context appContext = context.getApplicationContext();
        Logger.getErrorLogsHandler().addOnLoggedListener(new LogsHandler.OnLoggedListener() {
            @Override
            public Priority[] filterPriorities() {
                return new Priority[]{Priority.WARN, Priority.ERROR};
            }

            @Override
            public void onLogged(final LogLine logLine) {
                if (!Log.isInDebugMode()) return;

                JobUtils.runOnMainThread(() -> ErrorInfoActivity.start(appContext, logLine));
            }
        });
    }

}

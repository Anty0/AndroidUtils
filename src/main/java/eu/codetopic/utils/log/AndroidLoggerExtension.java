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

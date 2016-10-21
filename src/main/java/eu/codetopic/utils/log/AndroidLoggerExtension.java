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

                JobUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ErrorInfoActivity.start(appContext, logLine);
                    }
                });
            }
        });
    }

}

package eu.codetopic.utils.log;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import eu.codetopic.utils.log.base.DefaultTarget;
import eu.codetopic.utils.log.base.LogTarget;
import eu.codetopic.utils.log.base.Priority;

public final class Logger {

    private static final String LOG_TAG = "Logger";
    private static final ErrorLogsHandler ERROR_LOG = new ErrorLogsHandler();
    private static final DebugModeManager DEBUG_MODE = new DebugModeManager();
    private static Context APP_CONTEXT = null;
    @NonNull private static LogTarget TARGET = new DefaultTarget();

    private Logger() {
    }

    @MainThread
    public static void initialize(@NonNull Context context) {
        if (APP_CONTEXT != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        APP_CONTEXT = context.getApplicationContext();
    }

    @Nullable
    static Context getAppContext() {
        return APP_CONTEXT;
    }

    public static ErrorLogsHandler getErrorLogsHandler() {
        return ERROR_LOG;
    }

    public static DebugModeManager getDebugModeManager() {
        return DEBUG_MODE;
    }

    public static boolean isInDebugMode() {
        return DEBUG_MODE.isInDebugMode();
    }

    @NonNull
    public static LogTarget getLogTarget() {
        return TARGET;
    }

    public static void setLogTarget(@NonNull LogTarget target) {
        TARGET = target;
    }

    static void println(Priority priority, String tag, String msg) {
        if (!isInDebugMode()) return;
        TARGET.println(priority, tag, msg);
    }

}

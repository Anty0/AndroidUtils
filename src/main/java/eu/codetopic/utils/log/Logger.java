package eu.codetopic.utils.log;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.log.base.DefaultTarget;
import eu.codetopic.utils.log.base.LogLine;
import eu.codetopic.utils.log.base.LogTarget;

public final class Logger {

    private static final String LOG_TAG = "Logger";
    private static final List<LogLine> LOG_LINES_CACHE = new ArrayList<>();// TODO: 5.9.16 write to and read from session files in debug activity
    private static final ErrorLogsHandler ERROR_LOG = new ErrorLogsHandler();
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

    @NonNull
    public static LogTarget getLogTarget() {
        return TARGET;
    }

    public static void setLogTarget(@NonNull LogTarget target) {
        TARGET = target;
    }

    public static String getLogLinesCache() {
        StringBuilder sb = new StringBuilder();
        synchronized (LOG_LINES_CACHE) {
            for (LogLine logLine : LOG_LINES_CACHE) {
                sb.append(logLine).append('\n');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    static void println(LogLine logLine) {
        ERROR_LOG.onLogged(logLine);

        if (!Log.isInDebugMode()) return;
        synchronized (LOG_LINES_CACHE) {
            LOG_LINES_CACHE.add(logLine);
        }
        TARGET.println(logLine);
    }

}

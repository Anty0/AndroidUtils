package eu.codetopic.utils.log;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.log.base.LogLine;
import eu.codetopic.utils.log.base.Priority;
import eu.codetopic.utils.thread.JobUtils;

import static eu.codetopic.utils.log.base.Priority.ERROR;
import static eu.codetopic.utils.log.base.Priority.WARN;

public final class ErrorLogsHandler {

    private static final String LOG_TAG = "ErrorLogsHandler";

    private final List<OnErrorLoggedListener> listeners = new ArrayList<>();

    ErrorLogsHandler() {
    }

    public synchronized void addOnErrorLoggedListener(OnErrorLoggedListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeOnErrorLoggedListener(OnErrorLoggedListener listener) {
        listeners.remove(listener);
    }

    synchronized void onLogged(final LogLine logLine) {
        Priority priority = logLine.getPriority();
        try {
            if (ERROR.equals(priority) || WARN.equals(priority)) {
                final Context appContext = Logger.getAppContext();
                if (appContext == null || !Log.isInDebugMode()) return;

                JobUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ErrorInfoActivity.start(appContext, logLine);
                    }
                });
            }
        } finally {
            if (ERROR.equals(priority)) {
                for (OnErrorLoggedListener listener : listeners)
                    listener.onError(logLine);
            }
        }
    }

    public interface OnErrorLoggedListener {

        void onError(LogLine logLine);
    }

}

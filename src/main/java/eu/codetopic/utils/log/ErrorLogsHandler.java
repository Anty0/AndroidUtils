package eu.codetopic.utils.log;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.thread.JobUtils;

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

    synchronized void onErrorLogged(String tag, String msg, @Nullable Throwable tr) {
        try {
            final Context appContext = Logger.getAppContext();
            if (appContext == null || !Log.isInDebugMode()) return;
            final StringBuilder sb = new StringBuilder()
                    .append(tag).append(": ").append(msg).append('\n')
                    .append(android.util.Log.getStackTraceString(tr));

            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    ErrorInfoActivity.start(appContext, sb.toString());
                }
            });
        } finally {
            for (OnErrorLoggedListener listener : listeners)
                listener.onError(tag, msg, tr);
        }
    }

    public interface OnErrorLoggedListener {

        void onError(String tag, String msg, @Nullable Throwable t);
    }

}

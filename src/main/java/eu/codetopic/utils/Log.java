package eu.codetopic.utils;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.module.data.DebugProviderData;
import eu.codetopic.utils.module.getter.DataGetter;
import eu.codetopic.utils.module.data.ModuleDataManager;

/**
 * Mock Log implementation for testing on non android host.
 */
public class Log {

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;
    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;
    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;
    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;
    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;
    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;
    /**
     * @hide
     */
    public static final int LOG_ID_MAIN = 0;
    /**
     * @hide
     */
    public static final int LOG_ID_RADIO = 1;
    /**
     * @hide
     */
    public static final int LOG_ID_EVENTS = 2;
    /**
     * @hide
     */
    public static final int LOG_ID_SYSTEM = 3;
    /**
     * @hide
     */
    public static final int LOG_ID_CRASH = 4;
    private static final List<OnErrorLoggedListener> listeners = new ArrayList<>();
    private static boolean INITIALIZED = false;
    private static boolean DEBUG_MODE = true;

    private Log() {
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        return println(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int v(String tag, String msg, Throwable tr) {
        return println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        return println(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int d(String tag, String msg, Throwable tr) {
        return println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {
        return println(INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int i(String tag, String msg, Throwable tr) {
        return println(INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        return println(WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int w(String tag, String msg, Throwable tr) {
        return println(WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param tr  An exception to log
     */
    public static int w(String tag, Throwable tr) {
        return println(WARN, tag, getStackTraceString(tr));
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        try {
            return println(ERROR, tag, msg);
        } finally {
            for (OnErrorLoggedListener listener : listeners)
                listener.onError(tag, msg, null);
        }
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static int e(String tag, String msg, Throwable tr) {
        try {
            return println(LOG_ID_MAIN, ERROR, tag, msg + '\n' + getStackTraceString(tr));
        } finally {
            for (OnErrorLoggedListener listener : listeners)
                listener.onError(tag, msg, tr);
        }
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * Low-level logging call.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message.  It usually identifies
     *                 the class or activity where the log call occurs.
     * @param msg      The message you would like logged.
     * @return The number of bytes written.
     */
    public static int println(int priority, String tag, String msg) {
        return println(LOG_ID_MAIN, priority, tag, msg);
    }

    /**
     * @hide
     */
    @SuppressWarnings("unused")
    public static int println(int bufID, int priority, String tag, String msg) {
        return DEBUG_MODE ? android.util.Log.println(priority, tag, msg) : 0;
    }

    public static boolean isInDebugMode() {
        return DEBUG_MODE;
    }

    public static synchronized void initDebugMode(Application app, @NonNull final DataGetter
            <? extends DebugProviderData> debugDataGetter) {// TODO: 8.3.16 initialize Log in ApplicationBase
        if (INITIALIZED) throw new IllegalStateException("Log is still initialized");
        INITIALIZED = true;

        app.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DEBUG_MODE = debugDataGetter.get().isDebugMode();
            }
        }, new IntentFilter(ModuleDataManager.getBroadcastActionChanged(debugDataGetter.get())));
        DEBUG_MODE = debugDataGetter.get().isDebugMode();
    }

    public static synchronized void addOnErrorLoggedListener(OnErrorLoggedListener listener) {// TODO: 25.3.16 add error listener in ApplicationBase
        listeners.add(listener);
    }

    public static synchronized void removeOnErrorLoggedListener(OnErrorLoggedListener listener) {
        listeners.remove(listener);
    }

    public interface OnErrorLoggedListener {

        void onError(String tag, String msg, @Nullable Throwable t);
    }
}

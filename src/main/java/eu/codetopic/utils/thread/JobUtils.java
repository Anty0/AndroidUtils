package eu.codetopic.utils.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import eu.codetopic.utils.Log;

public class JobUtils {

    private static final String LOG_TAG = "JobUtils";

    private static Handler HANDLER;
    private static Thread MAIN_THREAD;

    public synchronized static void initialize(Context context) {
        if (HANDLER != null || MAIN_THREAD != null)
            throw new IllegalStateException(LOG_TAG + " is still initialized");

        Looper looper = context.getApplicationContext().getMainLooper();
        HANDLER = new Handler(looper);
        MAIN_THREAD = looper.getThread();
    }

    public static void runOnMainThread(Runnable action) {
        if (!isOnMainThread()) {
            postOnMainThread(action);
            return;
        }
        action.run();
    }

    public static boolean isOnMainThread() {
        return Thread.currentThread() == MAIN_THREAD;
    }

    public static void postOnMainThread(Runnable action) {
        HANDLER.post(action);
    }

    public static void runOnContextThread(@Nullable Context context, Runnable action) {
        if (context == null) {
            runOnMainThread(action);
            return;
        }

        if (!isOnContextThread(context)) {
            postOnContextThread(context, action);
            return;
        }
        action.run();
    }

    public static boolean isOnContextThread(@Nullable Context context) {
        if (context == null) return isOnMainThread();
        return Thread.currentThread() == context.getMainLooper().getThread();
    }

    public static void postOnContextThread(@Nullable Context context, Runnable action) {
        if (context == null) {
            postOnMainThread(action);
            return;
        }

        new Handler(context.getMainLooper()).post(action);
    }

    public static void postOnViewThread(@Nullable View view, Runnable action) {
        if (view == null) {
            postOnMainThread(action);
            return;
        }

        view.post(action);
    }

    public static boolean threadSleep(long time) {
        try {
            Thread.sleep(time);
            return true;
        } catch (InterruptedException e) {
            Log.d(LOG_TAG, "threadSleep", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static boolean threadSleep(long milis, int nanos) {
        try {
            Thread.sleep(milis, nanos);
            return true;
        } catch (InterruptedException e) {
            Log.d(LOG_TAG, "threadSleep", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

}

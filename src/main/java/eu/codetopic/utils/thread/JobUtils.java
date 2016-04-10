package eu.codetopic.utils.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import eu.codetopic.utils.Log;

public class JobUtils {

    private static final String LOG_TAG = "JobUtils";

    private static Handler HANDLER;
    private static Thread UI_THREAD;

    public synchronized static void initialize(Context context) {
        Looper looper = context.getMainLooper();
        HANDLER = new Handler(looper);
        UI_THREAD = looper.getThread();
    }

    public static boolean isOnMainThread() {
        return Thread.currentThread() == UI_THREAD;
    }

    public static void runOnMainThread(Runnable action) {
        if (!isOnMainThread()) {
            postOnMainThread(action);
            return;
        }
        action.run();
    }

    public static void postOnMainThread(Runnable action) {
        HANDLER.post(action);
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

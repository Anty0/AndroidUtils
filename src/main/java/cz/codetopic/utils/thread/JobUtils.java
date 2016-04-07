package cz.codetopic.utils.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class JobUtils {

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

}

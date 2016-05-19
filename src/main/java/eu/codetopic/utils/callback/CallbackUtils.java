package eu.codetopic.utils.callback;

import android.support.annotation.Nullable;

import eu.codetopic.utils.thread.JobUtils;

public class CallbackUtils {

    private static final String LOG_TAG = "CallbackUtils";

    public static <R> void doCallbackWork(@Nullable ActionCallback<R> callback, CallbackWork<R> work) {
        try {
            doCallbackWorkWithThrow(callback, work);
        } catch (Throwable ignored) {
        }
    }

    public static <R> void doCallbackWorkWithThrow(@Nullable final ActionCallback<R> callback, CallbackWork<R> work) throws Throwable {
        R result = null;
        Throwable caught = null;
        try {
            result = work.work();
        } catch (Throwable t) {
            caught = t;
        }

        final R finalResult = result;
        final Throwable finalCaught = caught;
        if (callback != null)
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onActionCompleted(finalResult, finalCaught);
                }
            });
        if (finalCaught != null) throw finalCaught;

    }

    public interface CallbackWork<R> {

        R work() throws Throwable;
    }

}

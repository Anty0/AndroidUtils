/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import eu.codetopic.java.utils.log.Log;

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

    public static boolean runOnMainThread(Runnable action) {
        if (!isOnMainThread()) return postOnMainThread(action);

        action.run();
        return true;
    }

    public static boolean isOnMainThread() {
        return Thread.currentThread() == MAIN_THREAD;
    }

    public static boolean postOnMainThread(Runnable action) {
        return HANDLER.post(action);
    }

    public static boolean runOnContextThread(@Nullable Context context, Runnable action) {
        if (context == null) return runOnMainThread(action);

        if (!isOnContextThread(context))
            return postOnContextThread(context, action);

        action.run();
        return true;
    }

    public static boolean isOnContextThread(@Nullable Context context) {
        if (context == null) return isOnMainThread();
        return Thread.currentThread() == context.getMainLooper().getThread();
    }

    public static boolean postOnContextThread(@Nullable Context context, Runnable action) {
        return context != null && new Handler(context.getMainLooper()).post(action)
                || postOnMainThread(action);
    }

    public static boolean postOnViewThread(@Nullable View view, Runnable action) {
        return view != null && view.post(action)
                || postOnMainThread(action);
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

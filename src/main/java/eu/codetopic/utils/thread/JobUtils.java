/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

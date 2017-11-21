/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.callback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.thread.JobUtils;

public class CallbackUtils {

    private static final String LOG_TAG = "CallbackUtils";

    public static <R> void doCallbackWork(@NonNull WeakReference<Context> contextRef,
                                          @Nullable ActionCallback<R> callback,
                                          CallbackWork<R> work) {
        try {
            doCallbackWorkWithThrow(contextRef, callback, work);
        } catch (Throwable ignored) {
        }
    }

    public static <R> void doCallbackWorkWithThrow(@NonNull WeakReference<Context> contextRef,
                                                   @Nullable final ActionCallback<R> callback,
                                                   CallbackWork<R> work) throws Throwable {
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
            JobUtils.runOnContextThread(contextRef.get(), new Runnable() {
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

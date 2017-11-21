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

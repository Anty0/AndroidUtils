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

package eu.codetopic.utils.thread;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

/**
 * Deprecated. Use NetworkJob instead.
 */
@Deprecated
@SuppressWarnings("deprecation")
public final class ContextThread<C extends Context> {

    private static final String LOG_TAG = "ContextThread";

    private final Context appContext;
    private final WeakReference<C> reference;
    private final LoadingVH loadingHolder;

    private ContextThread(@NonNull C context, @Nullable LoadingVH loadingHolder) {
        this.appContext = context.getApplicationContext();
        this.reference = new WeakReference<>(context);
        this.loadingHolder = loadingHolder;
    }

    public static <C extends Context, D> void work(@NonNull C context,
                                                   @NonNull final Work<C, D> runnable) {
        work(context, null, runnable);
    }

    public static <C extends Context, D> void work(@NonNull C context, @Nullable LoadingVH loadingHolder,
                                                   @NonNull final Work<C, D> runnable) {
        new ContextThread<>(context, loadingHolder).start(runnable);
    }

    public <D> void start(@NonNull final Work<C, D> runnable) {
        if (loadingHolder != null) loadingHolder.showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                D result = null;
                Throwable throwable = null;
                try {
                    result = runnable.work(appContext);
                } catch (final Throwable t) {
                    Log.d(LOG_TAG, "start", t);
                    throwable = t;
                } finally {
                    final D finalResult = result;
                    final Throwable finalThrowable = throwable;
                    JobUtils.runOnContextThread(reference.get(), new Runnable() {
                        @Override
                        public void run() {
                            try {
                                C c = reference.get();
                                if (c != null) {
                                    if (finalThrowable != null)
                                        runnable.error(c, finalThrowable);
                                    else runnable.update(c, finalResult);
                                }
                            } finally {
                                if (loadingHolder != null) loadingHolder.hideLoading();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public interface Work<C extends Context, D> {

        @WorkerThread
        D work(@NonNull Context appContext) throws Throwable;

        @UiThread
        void update(@NonNull C context, D data);

        @UiThread
        void error(@NonNull C context, Throwable throwable);
    }

}

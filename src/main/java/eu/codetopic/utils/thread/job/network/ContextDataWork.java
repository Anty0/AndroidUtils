package eu.codetopic.utils.thread.job.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.thread.JobUtils;

public abstract class ContextDataWork<C extends Context, D> implements NetworkJob.Work {

    private static final String LOG_TAG = NetworkJob.LOG_TAG + "$ContextDataWork";

    @NonNull
    private final Context appContext;
    @NonNull
    private final WeakReference<C> contextRef;

    public ContextDataWork(@NonNull C context) {
        this.appContext = context.getApplicationContext();
        this.contextRef = new WeakReference<>(context);
    }

    @NonNull
    public Context getAppContext() {
        return appContext;
    }

    @Nullable
    @UiThread
    public C getContext() {
        return contextRef.get();
    }

    @NonNull
    public WeakReference<C> getContextRef() {
        return contextRef;
    }

    @Override
    @WorkerThread
    public final void run() throws Throwable {
        D result = null;
        Throwable throwable = null;
        try {
            result = work();
        } catch (final Throwable t) {
            Log.d(LOG_TAG, "run", t);
            throwable = t;
        } finally {
            final D fResult = result;
            final Throwable fThrowable = throwable;
            JobUtils.runOnContextThread(getContextRef().get(), new Runnable() {
                @Override
                public void run() {
                    result(fResult, fThrowable);
                }
            });
        }
    }

    @WorkerThread
    public abstract D work() throws Throwable;

    @UiThread
    public abstract void result(@Nullable D data, @Nullable Throwable tr);
}

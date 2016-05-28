package eu.codetopic.utils.thread.job.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.Dao;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.thread.JobUtils;

public abstract class DatabaseCallbackWork<W, D, T, ID> implements DatabaseWork<T, ID> {

    private static final String LOG_TAG = "DatabaseCallbackWork";

    private final WeakReference<Context> contextRef;
    private final WeakReference<W> weakDataRef;

    public DatabaseCallbackWork(@NonNull Context context, W weakData) {
        this.contextRef = new WeakReference<>(context);
        this.weakDataRef = new WeakReference<>(weakData);
    }

    @Override
    public final void run(Dao<T, ID> dao) throws Throwable {
        D result = null;
        Throwable throwable = null;
        try {
            result = work(dao);
        } catch (final Throwable t) {
            Log.d(LOG_TAG, "start", t);
            throwable = t;
        } finally {
            final D finalResult = result;
            final Throwable finalThrowable = throwable;
            JobUtils.runOnContextThread(contextRef.get(), new Runnable() {
                @Override
                public void run() {
                    finish(weakDataRef.get(), finalResult, finalThrowable);
                }
            });
        }
    }

    @WorkerThread
    public abstract D work(Dao<T, ID> dao) throws Throwable;

    @UiThread
    public abstract void finish(@Nullable W weakData, D result, Throwable throwable);

}

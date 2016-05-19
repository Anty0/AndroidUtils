package eu.codetopic.utils.thread;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.activity.loading.LoadingViewHolder;

public final class ContextThread<C extends Context> {

    private final Context appContext;
    private final WeakReference<C> reference;
    private final LoadingViewHolder loadingHolder;

    private ContextThread(@NonNull C context, @Nullable LoadingViewHolder loadingHolder) {
        this.appContext = context.getApplicationContext();
        this.reference = new WeakReference<>(context);
        this.loadingHolder = loadingHolder;
    }

    public static <C extends Context, D> void work(@NonNull C context,
                                                   @NonNull final Work<C, D> runnable) {
        work(context, null, runnable);
    }

    public static <C extends Context, D> void work(@NonNull C context, @Nullable LoadingViewHolder loadingHolder,
                                                   @NonNull final Work<C, D> runnable) {
        new ContextThread<>(context, loadingHolder).start(runnable);
    }

    public <D> void start(@NonNull final Work<C, D> runnable) {
        if (loadingHolder != null) loadingHolder.showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final D data = runnable.work(appContext);
                    JobUtils.runOnContextThread(reference.get(), new Runnable() {
                        @Override
                        public void run() {
                            C c = reference.get();
                            if (c != null) runnable.update(c, data);
                        }
                    });
                } catch (final Throwable throwable) {
                    throwable.printStackTrace();
                    JobUtils.runOnContextThread(reference.get(), new Runnable() {
                        @Override
                        public void run() {
                            C c = reference.get();
                            if (c != null) runnable.error(c, throwable);
                        }
                    });
                } finally {
                    if (loadingHolder != null) loadingHolder.hideLoading();
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

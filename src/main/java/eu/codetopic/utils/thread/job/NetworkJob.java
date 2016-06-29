package eu.codetopic.utils.thread.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.birbit.android.jobqueue.Params;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.view.holder.loading.LoadingVH;

public class NetworkJob extends LoadingJob {

    private static final String LOG_TAG = "NetworkJob";
    private static final String JOB_NETWORK_GROUP_NAME_ADD = ".NETWORK_GROUP";

    @Nullable
    private final Work job;

    protected NetworkJob(@Nullable LoadingVH loadingViewHolder, @Nullable Class<?> syncCls) {
        this(loadingViewHolder, syncCls, null);
    }

    public NetworkJob(@Nullable Class<?> syncCls, @Nullable Work work) {
        this(null, syncCls, work);
    }

    public NetworkJob(@Nullable LoadingVH loadingViewHolder,
                      @Nullable Class<?> syncCls, @Nullable Work work) {
        super(generateParams(syncCls), loadingViewHolder);
        job = work;
    }

    public static String start(@NonNull NetworkJob job) {
        SingletonJobManager.getInstance().addJobInBackground(job);
        return job.getId();
    }

    public static String generateNetworkJobGroupNameFor(Class<?> syncCls) {
        return syncCls.getName() + JOB_NETWORK_GROUP_NAME_ADD;
    }

    private static Params generateParams(@Nullable Class<?> syncCls) {
        Params params = new Params(Constants.JOB_PRIORITY_NETWORK).requireNetwork();
        if (syncCls != null) params.groupBy(generateNetworkJobGroupNameFor(syncCls));
        return params;
    }

    @Override
    public void onStart() throws Throwable {
        if (job != null) job.run();
    }

    @Override
    protected int getRetryLimit() {
        return getViewHolder() == null ? super.getRetryLimit() : Constants.JOB_REPEAT_COUNT_NETWORK;
    }

    public interface Work {

        @WorkerThread
        void run() throws Throwable;
    }

    public static abstract class ContextDataWork<C extends Context, D> implements Work {

        private static final String LOG_TAG = NetworkJob.LOG_TAG + "$ContextDataWork";

        @NonNull private final Context appContext;
        @NonNull private final WeakReference<C> contextRef;

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

    public static abstract class ContextUpdateWork<C extends Context, D> extends ContextDataWork<C, D> {

        public ContextUpdateWork(@NonNull C context) {
            super(context);
        }

        @Override
        public final void result(@Nullable D data, @Nullable Throwable tr) {
            if (tr != null) error(tr);
            else update(data);
        }

        @UiThread
        public void update(D data) {
            C context = getContext();
            if (context != null) updateContext(context, data);
            else updateNoContext(data);
        }

        @UiThread
        public void updateContext(@NonNull C context, D data) {

        }

        @UiThread
        public void updateNoContext(D data) {

        }

        @UiThread
        public void error(Throwable tr) {
            C context = getContext();
            if (context != null) errorContext(context, tr);
            else errorNoContext(tr);
        }

        @UiThread
        public void errorContext(@NonNull C context, Throwable tr) {

        }

        @UiThread
        public void errorNoContext(Throwable tr) {

        }
    }
}

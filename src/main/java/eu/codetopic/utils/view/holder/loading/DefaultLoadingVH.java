package eu.codetopic.utils.view.holder.loading;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.R;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.progress.ProgressInfo;
import eu.codetopic.utils.thread.progress.ProgressReporter;
import eu.codetopic.utils.thread.progress.ProgressReporterImpl;

public class DefaultLoadingVH extends ProgressLoadingVH {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.loading_base;
    @IdRes protected static final int CONTENT_VIEW_ID = R.id.base_loadable_content;
    @IdRes protected static final int LOADING_VIEW_ID = R.id.base_loading;
    @IdRes protected static final int CIRCLE_LOADING_VIEW_ID = R.id.base_loading_circle;
    @IdRes protected static final int HORIZONTAL_LOADING_VIEW_ID = R.id.base_loading_horizontal;

    private static final String LOG_TAG = "DefaultLoadingVH";

    @NonNull
    @Override
    protected LoadingWrappingInfo getWrappingInfo() {
        return new LoadingWrappingInfo(LOADING_LAYOUT_ID, CONTENT_VIEW_ID, LOADING_VIEW_ID);
    }

    @Override
    protected DualProgressReporter generateProgressReporter() {
        return doUpdateReporter(new DualProgressReporter());
    }

    @UiThread
    @Override
    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof DualProgressReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        doUpdateReporter((DualProgressReporter) reporter);
    }

    private DualProgressReporter doUpdateReporter(DualProgressReporter reporter) {
        reporter.loadingViewRef = getLoadingViewRef();
        reporter.onChange(reporter.getProgressInfo());
        return reporter;
    }

    private static class DualProgressReporter extends ProgressReporterImpl {

        WeakReference<View> loadingViewRef;

        @Override
        protected void onChange(final ProgressInfo info) {
            JobUtils.postOnViewThread(loadingViewRef.get(), new Runnable() {
                @Override
                public void run() {
                    View loadingView = loadingViewRef.get();
                    if (loadingView != null) {
                        View circleProgress = loadingView.findViewById(CIRCLE_LOADING_VIEW_ID);
                        ProgressBar horizontalProgress = (ProgressBar) loadingView
                                .findViewById(HORIZONTAL_LOADING_VIEW_ID);

                        circleProgress.setVisibility(info.isShowingProgress() ? View.GONE : View.VISIBLE);
                        horizontalProgress.setVisibility(info.isShowingProgress() ? View.VISIBLE : View.GONE);
                        horizontalProgress.setMax(info.getMaxProgress());
                        horizontalProgress.setProgress(info.getProgress());
                        horizontalProgress.setIndeterminate(info.isIntermediate());
                    }
                }
            });
        }
    }
}

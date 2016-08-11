package com.codetopic.utils.activity.loading;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.codetopic.utils.thread.progress.ProgressBarReporter;
import com.codetopic.utils.thread.progress.ProgressReporter;

/**
 * Use ProgressLoadingVH instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingViewHolderWithProgress extends LoadingViewHolderImpl {

    private static final String LOG_TAG = "LoadingViewHolderWithProgress";

    private ProgressReporter progressReporter = null;

    public ProgressReporter getProgressReporter() {
        if (progressReporter == null)
            progressReporter = generateProgressReporter();

        return progressReporter;
    }

    protected ProgressReporter generateProgressReporter() {
        View loading = getLoadingView();
        return new ProgressBarReporter(loading instanceof ProgressBar
                ? (ProgressBar) loading : null);
    }

    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof ProgressBarReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        View loading = getLoadingView();
        ((ProgressBarReporter) progressReporter).setProgressBar(loading instanceof ProgressBar
                ? (ProgressBar) loading : null);
    }

    @Override
    protected void onUpdateMainView(@Nullable View newMainView) {
        super.onUpdateMainView(newMainView);
        if (progressReporter != null)
            updateProgressReporter(progressReporter);
    }
}

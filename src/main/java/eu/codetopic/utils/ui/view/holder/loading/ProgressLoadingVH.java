package eu.codetopic.utils.ui.view.holder.loading;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;

import eu.codetopic.utils.thread.progress.ProgressBarReporter;
import eu.codetopic.utils.thread.progress.ProgressReporter;

public abstract class ProgressLoadingVH extends LoadingVHImpl {

    private static final String LOG_TAG = "ProgressLoadingVH";

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

    @UiThread
    protected void updateProgressReporter(ProgressReporter reporter) {
        if (!(reporter instanceof ProgressBarReporter))
            throw new ClassCastException("Can't update progress of " + reporter
                    + ", please override method updateProgressReporter() and create your own updater.");

        View loading = getLoadingView();
        ((ProgressBarReporter) progressReporter).setProgressBar(loading instanceof ProgressBar
                ? (ProgressBar) loading : null);
    }

    @UiThread
    @Override
    protected void onUpdateView(@Nullable View view) {
        super.onUpdateView(view);
        if (progressReporter != null)
            updateProgressReporter(progressReporter);
    }

}

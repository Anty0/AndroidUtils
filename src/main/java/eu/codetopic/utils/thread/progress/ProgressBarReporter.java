package eu.codetopic.utils.thread.progress;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.thread.JobUtils;

public class ProgressBarReporter extends ProgressReporterImpl {

    private static final String LOG_TAG = "ProgressBarReporter";

    private WeakReference<ProgressBar> progressBarRef;

    public ProgressBarReporter() {
        setProgressBar((ProgressBar) null);
    }

    public ProgressBarReporter(@Nullable ProgressBar progressBar) {
        setProgressBar(progressBar);
    }

    public ProgressBarReporter(@NonNull WeakReference<ProgressBar> progressBarRef) {
        setProgressBar(progressBarRef);
    }

    public void setProgressBar(@Nullable ProgressBar progressBar) {
        setProgressBar(new WeakReference<>(progressBar));
    }

    public void setProgressBar(@NonNull WeakReference<ProgressBar> progressBarRef) {
        this.progressBarRef = progressBarRef;
    }

    @Override
    protected void onChange() {
        final ProgressInfo info = getProgressInfo();
        JobUtils.postOnViewThread(progressBarRef.get(), new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = progressBarRef.get();
                if (progressBar != null) {
                    progressBar.setMax(info.getMaxProgress());
                    progressBar.setProgress(info.getProgress());
                    progressBar.setIndeterminate(info.isIntermediate());
                }
            }
        });
    }
}

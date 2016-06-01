package eu.codetopic.utils.thread.progress;

import android.support.annotation.WorkerThread;

public abstract class ProgressReporterImpl implements ProgressReporter {

    private static final String LOG_TAG = "ProgressReporterImpl";

    private boolean showingProgress = false;
    private int max = 100;
    private int progress = 0;
    private boolean intermediate = true;

    public boolean isShowingProgress() {
        return showingProgress;
    }

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isIntermediate() {
        return intermediate;
    }

    @Override
    public synchronized void setIntermediate(boolean intermediate) {
        this.intermediate = intermediate;
        update();
    }

    @Override
    public synchronized void startShowingProgress() {
        showingProgress = true;
        max = 100;
        progress = 0;
        intermediate = false;
        update();
    }

    @Override
    public synchronized void stopShowingProgress() {
        max = 100;
        progress = 0;
        intermediate = true;
        update();
        showingProgress = false;
    }

    @Override
    public synchronized void setMaxProgress(int max) {
        this.max = max;
        intermediate = false;
        update();
    }

    @Override
    public synchronized void reportProgress(int progress) {
        this.progress = progress;
        intermediate = false;
        update();
    }

    @Override
    public void stepProgress(int step) {
        reportProgress(progress + step);
    }

    @Override
    public ProgressInfo getProgressInfo() {
        return new ProgressInfoImpl(showingProgress, max, progress, intermediate);
    }

    @WorkerThread
    protected final synchronized void update() {
        if (!showingProgress) return;
        onChange();
    }

    @WorkerThread
    protected abstract void onChange();

}

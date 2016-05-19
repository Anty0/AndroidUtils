package eu.codetopic.utils.thread;

public interface ProgressReporter {

    void startShowingProgress();

    void stopShowingProgress();

    void setIntermediate(boolean intermediate);

    void setMaxProgress(int max);

    void reportProgress(int progress);

    void stepProgress(int step);

    ProgressInfo getProgressInfo();
}

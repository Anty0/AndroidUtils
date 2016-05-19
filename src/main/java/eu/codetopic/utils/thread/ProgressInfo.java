package eu.codetopic.utils.thread;

public interface ProgressInfo {

    boolean isIntermediate();

    int getMaxProgress();

    int getProgress();
}

package com.codetopic.utils.thread.progress;

public interface ProgressInfo {

    boolean isShowingProgress();

    boolean isIntermediate();

    int getMaxProgress();

    int getProgress();
}

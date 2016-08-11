package eu.codetopic.utils.thread.progress;

public class ProgressInfoImpl implements ProgressInfo {

    private final int maxProgress, progress;
    private final boolean showingProgress, intermediate;

    public ProgressInfoImpl(boolean showingProgress, int maxProgress, int progress, boolean intermediate) {
        this.showingProgress = showingProgress;
        this.maxProgress = maxProgress;
        this.progress = progress;
        this.intermediate = intermediate;
    }

    @Override
    public boolean isShowingProgress() {
        return showingProgress;
    }

    @Override
    public boolean isIntermediate() {
        return intermediate;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public int getProgress() {
        return progress;
    }
}

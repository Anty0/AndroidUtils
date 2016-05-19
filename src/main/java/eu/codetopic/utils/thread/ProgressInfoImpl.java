package eu.codetopic.utils.thread;

public class ProgressInfoImpl implements ProgressInfo {

    private final int maxProgress, progress;
    private final boolean intermediate;

    public ProgressInfoImpl(int maxProgress, int progress, boolean intermediate) {
        this.maxProgress = maxProgress;
        this.progress = progress;
        this.intermediate = intermediate;
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

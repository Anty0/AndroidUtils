package eu.codetopic.utils.thread;

/**
 * Created by anty on 31.3.16.
 *
 * @author anty
 */
public class ProgressReporterWrapper implements ProgressReporter {

    private final ProgressReporter mBase;

    public ProgressReporterWrapper(ProgressReporter base) {
        mBase = base;
    }

    @Override
    public void startShowingProgress() {
        mBase.startShowingProgress();
    }

    @Override
    public void stopShowingProgress() {
        mBase.stopShowingProgress();
    }

    @Override
    public void setIntermediate(boolean intermediate) {
        mBase.setIntermediate(intermediate);
    }

    @Override
    public void setMaxProgress(int max) {
        mBase.setMaxProgress(max);
    }

    @Override
    public void reportProgress(int progress) {
        mBase.reportProgress(progress);
    }

    @Override
    public void stepProgress(int step) {
        mBase.stepProgress(step);
    }

    @Override
    public ProgressInfo getProgressInfo() {
        return mBase.getProgressInfo();
    }
}

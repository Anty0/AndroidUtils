package eu.codetopic.utils.thread;

/**
 * Created by anty on 1.4.16.
 *
 * @author anty
 */
public interface ProgressInfo {

    boolean isIntermediate();

    int getMaxProgress();

    int getProgress();
}

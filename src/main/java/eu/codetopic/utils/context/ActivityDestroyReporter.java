package eu.codetopic.utils.context;

/**
 * Created by anty on 17.5.16.
 *
 * @author anty
 */
public interface ActivityDestroyReporter {

    void registerListener(ActivityDestroyListener listener);

    void unregisterListener(ActivityDestroyListener listener);
}

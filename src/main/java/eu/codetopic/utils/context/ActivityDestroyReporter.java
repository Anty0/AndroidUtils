package eu.codetopic.utils.context;

@Deprecated
@SuppressWarnings("deprecation")
public interface ActivityDestroyReporter {

    void registerListener(ActivityDestroyListener listener);

    void unregisterListener(ActivityDestroyListener listener);
}

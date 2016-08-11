package com.codetopic.utils.context;

public interface ActivityDestroyReporter {

    void registerListener(ActivityDestroyListener listener);

    void unregisterListener(ActivityDestroyListener listener);
}

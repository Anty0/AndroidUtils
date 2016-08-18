package eu.codetopic.utils.context;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@SuppressWarnings("deprecation")
public class DestroyReporterHelper implements ActivityDestroyReporter {

    private static final String LOG_TAG = "DestroyReporterHelper";

    private final List<ActivityDestroyListener> mListeners = new ArrayList<>();

    private boolean destroyed = false;

    public synchronized void registerListener(ActivityDestroyListener listener) {
        if (destroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mListeners.add(listener);
    }

    public synchronized void unregisterListener(ActivityDestroyListener listener) {
        if (destroyed) throw new IllegalStateException(LOG_TAG + " is destroyed");
        mListeners.remove(listener);
    }

    public synchronized void reportDestroy() {
        for (ActivityDestroyListener listener : mListeners)
            listener.onDestroy();
        destroyed = true;
        mListeners.clear();
    }

}

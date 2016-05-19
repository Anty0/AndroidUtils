package eu.codetopic.utils.context;

import eu.codetopic.utils.activity.modular.SimpleActivityCallBackModule;

/**
 * Created by anty on 17.5.16.
 *
 * @author anty
 */
public class ActivityDestroyReporterModule extends SimpleActivityCallBackModule implements ActivityDestroyReporter {

    private static final String LOG_TAG = "ActivityDestroyReporterModule";

    private final DestroyReporterHelper helper = new DestroyReporterHelper();

    public synchronized void registerListener(ActivityDestroyListener listener) {
        helper.registerListener(listener);
    }

    public synchronized void unregisterListener(ActivityDestroyListener listener) {
        helper.unregisterListener(listener);
    }

    @Override
    protected synchronized void onDestroy() {
        helper.reportDestroy();
        super.onDestroy();
    }
}

package cz.codetopic.utils;

import android.app.Application;

import cz.codetopic.utils.service.ServiceCommander;
import cz.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 22.3.16.
 *
 * @author anty
 */
public class UtilsInitializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobUtils.initialize(this);
    }

    @Override
    public void onLowMemory() {
        ServiceCommander.disconnectAndKillUnneeded();
        super.onLowMemory();
        System.runFinalization();
        System.gc();
    }

    @Override
    public void onTerminate() {
        ServiceCommander.disconnectAndStopAll();
        super.onTerminate();
    }
}

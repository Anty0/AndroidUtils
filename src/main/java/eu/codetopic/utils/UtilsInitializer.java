package eu.codetopic.utils;

import android.app.Application;

import eu.codetopic.utils.broadcast.BroadcastsConnector;
import eu.codetopic.utils.service.ServiceCommander;
import eu.codetopic.utils.thread.JobUtils;

public class UtilsInitializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.init(this);
        JobUtils.initialize(this);
        BroadcastsConnector.initialize(this);
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

package eu.codetopic.utils;

import android.app.Application;
import android.content.Context;
import android.os.UserManager;

import com.squareup.leakcanary.LeakCanary;

import eu.codetopic.utils.broadcast.BroadcastsConnector;
import eu.codetopic.utils.service.ServiceCommander;
import eu.codetopic.utils.thread.JobUtils;

public class UtilsInitializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            UserManager.class.getMethod("get", Context.class).invoke(null, this);//fixes system leak in android
        } catch (Exception ignored) {
        }

        android.util.Log.d(Utils.getApplicationLabel(this).toString(), "INITIALIZING {"
                + "\n    - BUILD_TYPE=" + BuildConfig.BUILD_TYPE
                + "\n    - VERSION_NAME=" + Utils.getApplicationVersionName(this)
                + "\n    - VERSION_CODE=" + Utils.getApplicationVersionCode(this)
                + "\n}");

        LeakCanary.install(this);

        Log.initialize(this);
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

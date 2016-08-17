package eu.codetopic.utils;

import android.app.Application;

import com.birbit.android.jobqueue.log.JqLog;
import com.squareup.leakcanary.LeakCanary;

import eu.codetopic.utils.broadcast.BroadcastsConnector;
import eu.codetopic.utils.ids.RequestCodes;
import eu.codetopic.utils.log.Logger;
import eu.codetopic.utils.log.base.JobQueueLogger;
import eu.codetopic.utils.service.ServiceCommander;
import eu.codetopic.utils.thread.JobUtils;

public class UtilsInitializer extends Application {

    /**
     * Methods that should be called in your Application onCreate:
     * - {@code eu.codetopic.utils.thread.job.SingletonJobManager.initialize() }
     * - {@code eu.codetopic.utils.data.database.singleton.SingletonDatabase.initialize() }
     * - {@code eu.codetopic.utils.locale.LocaleManager.initialize() }
     * - {@code eu.codetopic.utils.log.DebugModeManager.initDebugModeDetector() } using {@code Logger.getDebugModeManager() }
     * - {@code eu.codetopic.utils.log.DebugModeManager.setDebugModeEnabled() } using {@code Logger.getDebugModeManager() }
     * - {@code eu.codetopic.utils.log.ErrorLogsHandler.addOnErrorLoggedListener() } using {@code Logger.getErrorLogsHandler }
     * - @Deprecated {@code eu.codetopic.utils.notifications.manage.NotificationIdsManager.initialize() }
     * - {@code eu.codetopic.utils.notifications.ids.NotificationCase.initialize() }
     * - {@code eu.codetopic.utils.timing.TimedComponentsManager.initialize() }
     * - {@code eu.codetopic.utils.broadcast.BroadcastsConnector.connect() }
     */
    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d(Utils.getApplicationLabel(this).toString(), "INITIALIZING {"
                + "\n    - DEBUG=" + BuildConfig.DEBUG
                + "\n    - BUILD_TYPE=" + BuildConfig.BUILD_TYPE
                + "\n    - VERSION_NAME=" + Utils.getApplicationVersionName(this)
                + "\n    - VERSION_CODE=" + Utils.getApplicationVersionCode(this)
                + "\n}");

        LeakCanary.install(this);

        Logger.initialize(this);
        JqLog.setCustomLogger(new JobQueueLogger());
        NetworkManager.init(this);
        JobUtils.initialize(this);
        BroadcastsConnector.initialize(this);
        RequestCodes.initialize(this);
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

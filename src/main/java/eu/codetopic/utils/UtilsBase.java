package eu.codetopic.utils;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.birbit.android.jobqueue.log.JqLog;
import com.squareup.leakcanary.LeakCanary;

import eu.codetopic.utils.broadcast.BroadcastsConnector;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.log.Logger;
import eu.codetopic.utils.log.base.JobQueueLogger;
import eu.codetopic.utils.service.ServiceCommander;
import eu.codetopic.utils.thread.JobUtils;

@UiThread
public final class UtilsBase {

    private static final String LOG_TAG = "UtilsBase";

    private static ProcessProfile ACTIVE_PROFILE = null;

    private UtilsBase() {
    }

    public static void initialize(Application app, ProcessProfile... profiles) {
        if (ACTIVE_PROFILE != null)
            throw new IllegalStateException(LOG_TAG + " is still initialized");

        String processName = Utils.getCurrentProcessName();
        if (processName == null) {
            Log.e(LOG_TAG, "initialize", new RuntimeException(
                    "Can't get CurrentProcessName, using default ProcessName"));
            processName = app.getPackageName();
        }

        for (ProcessProfile profile : profiles) {
            if (!Objects.equals(processName, profile.getProcessName())) continue;
            setActiveProfile(app, profile);
            return;
        }
        Log.e(LOG_TAG, "initialize", new IllegalStateException("Can't find ProcessName ("
                + processName + "), in provided ProcessProfiles, using empty ProcessProfile"));
        setActiveProfile(app, new ProcessProfile(processName, InitType.DISABLE_UTILS));
    }

    private static void setActiveProfile(Application app, ProcessProfile activeProfile) {
        ACTIVE_PROFILE = activeProfile;
        completeInit(app);
    }

    public static ProcessProfile getActiveProfile() {
        return ACTIVE_PROFILE;
    }

    private static void completeInit(Application app) {
        android.util.Log.d(Utils.getApplicationLabel(app).toString(), "INITIALIZING {"
                + "\n    - PROCESS_NAME=" + ACTIVE_PROFILE.getProcessName()
                + "\n    - DEBUG=" + BuildConfig.DEBUG
                + "\n    - BUILD_TYPE=" + BuildConfig.BUILD_TYPE
                + "\n    - VERSION_NAME=" + Utils.getApplicationVersionName(app)
                + "\n    - VERSION_CODE=" + Utils.getApplicationVersionCode(app)
                + "\n}");//TODO: add here ACTIVE_PROFILE toString() result

        InitType initType = ACTIVE_PROFILE.getUtilsInitType();
        if (initType.isUtilsEnabled()) {
            LeakCanary.install(app);

            Logger.initialize(app);
            JqLog.setCustomLogger(new JobQueueLogger());
            NetworkManager.init(app);
            JobUtils.initialize(app);
            BroadcastsConnector.initialize(app);
            if (initType.isMutiProcessModeEnabled) {
                Identifiers.initialize(app);
            }

            app.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {

                }

                @Override
                public void onLowMemory() {
                    ServiceCommander.disconnectAndKillUnneeded();
                    System.runFinalization();
                    System.gc();
                }
            });
        }

        for (Runnable command : ACTIVE_PROFILE.getAdditionalCommands()) command.run();
    }
    
    public enum InitType {
        INIT_MULTI_PROCESS_MODE(true, true), INIT_NORMAL_MODE(true, false),
        DISABLE_UTILS(false, false);
        
        private final boolean utilsEnabled, multiProcessModeEnabled;
        
        public InitType(boolean utilsEnabled, boolean multiProcessModeEnabled) {
            this.utilsEnabled = utilsEnabled;
            this.multiProcessModeEnabled = multiProcessModeEnabled;
        }
        
        public boolean isUtilsEnabled() {
            return utilsEnabled;
        }
        
        public boolean isMultiProcessModeEnabled() {
            return multiProcessModeEnabled;
        }
    }

    public static final class ProcessProfile {

        @NonNull private final String processName;
        @NonNull private final InitType utilsInitType;
        private final Runnable[] additionalCommands;

        /**
         * Methods that should be called in additionalCommands:
         * - {@code eu.codetopic.utils.thread.job.SingletonJobManager.initialize() }
         * - {@code eu.codetopic.utils.data.database.singleton.SingletonDatabase.initialize() }
         * - {@code LocaleManager.initialize() }
         * - {@code eu.codetopic.utils.log.DebugModeManager.initDebugModeDetector() } using {@code Logger.getDebugModeManager() }
         * - {@code eu.codetopic.utils.log.DebugModeManager.setDebugModeEnabled() } using {@code Logger.getDebugModeManager() }
         * - {@code eu.codetopic.utils.log.ErrorLogsHandler.addOnErrorLoggedListener() } using {@code Logger.getErrorLogsHandler }
         * - {@code eu.codetopic.utils.timing.TimedComponentsManager.initialize() }
         * - {@code eu.codetopic.utils.broadcast.BroadcastsConnector.connect() }
         */
        public ProcessProfile(@NonNull String processName, @NonNull InitType utilsInitType,
                              Runnable... additionalCommands) {

            this.processName = processName;
            this.utilsInitType = utilsInitType;
            this.additionalCommands = additionalCommands;
        }

        @NonNull
        public String getProcessName() {
            return processName;
        }

        @NonNull
        public InitType getUtilsInitType() {
            return utilsInitType;
        }

        private Runnable[] getAdditionalCommands() {
            return additionalCommands;
        }
    }

}

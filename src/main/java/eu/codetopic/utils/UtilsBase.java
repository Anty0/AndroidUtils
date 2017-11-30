/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.birbit.android.jobqueue.log.JqLog;
import com.squareup.leakcanary.LeakCanary;

import java.util.Arrays;

import eu.codetopic.java.utils.ArrayTools;
import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.broadcast.BroadcastsConnector;
import eu.codetopic.utils.ids.Identifiers;
import eu.codetopic.utils.log.AndroidLoggerExtension;
import eu.codetopic.utils.log.JobQueueLogger;
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

        profiles = ArrayTools.add(profiles, new ProcessProfile(
                app.getPackageName() + ":leakcanary", InitType.NONE
        ));  // add LeakCanary process profile

        String processName = AndroidUtils.getCurrentProcessName();
        if (processName == null) {
            Log.e(LOG_TAG, "initialize", new RuntimeException(
                    "Can't get CurrentProcessName, using main process name"));
            processName = app.getPackageName();
        }

        for (ProcessProfile profile : profiles) {
            if (!Objects.equals(processName, profile.getProcessName())) continue;
            setActiveProfile(app, profile);
            return;
        }
        Log.e(LOG_TAG, "initialize", new IllegalStateException("Can't find ProcessName ("
                + processName + "), in provided ProcessProfiles, using empty ProcessProfile (utils will be disabled)"));
        setActiveProfile(app, new ProcessProfile(processName, InitType.NONE));
    }

    private static void setActiveProfile(Application app, ProcessProfile activeProfile) {
        ACTIVE_PROFILE = activeProfile;
        completeInit(app);
    }

    public static ProcessProfile getActiveProfile() {
        return ACTIVE_PROFILE;
    }

    private static void completeInit(Application app) {
        android.util.Log.d(AndroidUtils.getApplicationLabel(app).toString(), "INITIALIZING:"
                + "\n    - PROCESS_PROFILE=" + ACTIVE_PROFILE
                + "\n    - DEBUG=" + BuildConfig.DEBUG
                + "\n    - BUILD_TYPE=" + BuildConfig.BUILD_TYPE
                + "\n    - VERSION_NAME=" + AndroidUtils.getApplicationVersionName(app)
                + "\n    - VERSION_CODE=" + AndroidUtils.getApplicationVersionCode(app));

        InitType initType = ACTIVE_PROFILE.getUtilsInitType();
        if (initType.isEnabled()) {
            if (!LeakCanary.isInAnalyzerProcess(app)) {
                LeakCanary.install(app);
            }

            AndroidLoggerExtension.install(app);
            JqLog.setCustomLogger(new JobQueueLogger());
            LocalBroadcast.INSTANCE.initialize(app);
            NetworkManager.init(app);
            JobUtils.initialize(app);
            BroadcastsConnector.initialize(app);

            if (initType.isStorageUsed()) {
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
        PRIMARY_PROCESS(true, true), // Enable everything
        ANOTHER_PROCESS(true, false), // Enable, but leave Identifiers storage disabled
        NORMAL(true, true), // Enable everything (alias for PRIMARY_PROCESS)
        NONE(false, false); // Disable everything
        
        private final boolean enable, storeData;

        InitType(boolean enable, boolean storeData) {
            this.enable = enable;
            this.storeData = storeData;
        }
        
        public boolean isEnabled() {
            return enable;
        }
        
        public boolean isStorageUsed() {
            return storeData;
        }

        @Override
        public String toString() {
            return "InitType{" +
                    "enable=" + enable +
                    ", storeData=" + storeData +
                    '}';
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
         * - {@code eu.codetopic.java.utils.log.LogsHandler.addOnLoggedListener() } using {@code Logger.getErrorLogsHandler }
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

        @Override
        public String toString() {
            return "ProcessProfile{" +
                    "processName='" + processName + '\'' +
                    ", utilsInitType=" + utilsInitType +
                    ", additionalCommands=" + Arrays.toString(additionalCommands) +
                    '}';
        }
    }

}

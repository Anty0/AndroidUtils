package eu.codetopic.utils.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import eu.codetopic.utils.BuildConfig;
import eu.codetopic.utils.data.DebugProviderData;
import eu.codetopic.utils.data.getter.DataGetter;

public final class DebugModeManager {

    private static final String LOG_TAG = "DebugModeManager";

    private boolean DEBUG_MODE_DETECTOR_INITIALIZED = false;
    private boolean DEBUG_MODE = BuildConfig.DEBUG;

    DebugModeManager() {
    }

    @MainThread
    public void initDebugModeDetector(@NonNull final DataGetter<? extends DebugProviderData> debugDataGetter) {
        if (DEBUG_MODE_DETECTOR_INITIALIZED)
            throw new IllegalStateException(LOG_TAG + "s DebugModeDetector is still initialized");
        Context appContext = Logger.getAppContext();
        if (appContext == null) throw new IllegalStateException("Logger is not initialized");
        if (!debugDataGetter.hasDataChangedBroadcastAction())
            throw new IllegalArgumentException("debugDataGetter must have DataChangedBroadcastAction");
        DEBUG_MODE_DETECTOR_INITIALIZED = true;

        LocalBroadcastManager.getInstance(appContext).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DEBUG_MODE = debugDataGetter.get().isDebugMode();
            }
        }, new IntentFilter(debugDataGetter.getDataChangedBroadcastAction()));

        DEBUG_MODE = debugDataGetter.get().isDebugMode();
    }

    @MainThread
    public void setDebugModeEnabled(boolean debug) {
        if (DEBUG_MODE_DETECTOR_INITIALIZED) throw new IllegalStateException(LOG_TAG
                + " is using DebugModeDetector, so you can't modify DebugMode manually");
        DEBUG_MODE = debug;
    }

    public boolean isInDebugMode() {
        return DEBUG_MODE;
    }

}

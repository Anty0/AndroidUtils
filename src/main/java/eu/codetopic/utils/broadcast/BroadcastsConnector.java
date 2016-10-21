package eu.codetopic.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.codetopic.java.utils.Objects;
import eu.codetopic.java.utils.log.Log;

public final class BroadcastsConnector extends BroadcastReceiver {

    private static final String LOG_TAG = "BroadcastsConnector";
    private static final BroadcastsConnector mInstance = new BroadcastsConnector();
    private static final HashMap<String, List<Connection>> mConnections = new HashMap<>();
    private static final HashMap<String, List<Connection>> mGroups = new HashMap<>();
    private static Context mContext = null;
    private static boolean mRegistered = false;

    private BroadcastsConnector() {
    }

    public static synchronized void initialize(@NonNull Context context) {
        if (mContext != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mContext = context.getApplicationContext();
    }

    public static synchronized void connect(@NonNull String actionFrom, @NonNull Connection connection) {
        try {
            List<Connection> connections = mConnections.get(actionFrom);
            if (connections == null) {
                connections = new ArrayList<>();
                mConnections.put(actionFrom, connections);
            }
            connections.add(connection);

            String groupName = connection.getGroupName();
            if (groupName != null) {
                connections = mGroups.get(groupName);
                if (connections == null) {
                    connections = new ArrayList<>();
                    mGroups.put(groupName, connections);
                }
                connections.add(connection);
            }
        } finally {
            registerBroadcast();
        }
    }

    public static synchronized boolean disconnect(@NonNull String actionFrom, @NonNull Connection connection) {
        try {
            List<Connection> connections = mGroups.get(connection.getGroupName());
            if (connections != null) connections.remove(connection);

            connections = mConnections.get(actionFrom);
            return connections != null && connections.remove(connection);
        } finally {
            registerBroadcast();
        }
    }

    public static synchronized boolean disconnectAll(@NonNull String actionFrom) {
        try {
            List<Connection> connections = mConnections.remove(actionFrom);
            if (connections == null) return false;
            for (Connection connection : connections) {
                List<Connection> groupConnections = mGroups.get(connection.getGroupName());
                if (groupConnections != null) groupConnections.remove(connection);
            }
            return true;
        } finally {
            registerBroadcast();
        }
    }

    public static synchronized void setGroupEnabled(@NonNull String groupName, boolean enabled) {
        List<Connection> connections = mGroups.get(groupName);
        if (connections == null) return;
        for (Connection connection : connections)
            connection.setEnabled(enabled);
    }

    public static synchronized void skipOneIntent(@NonNull String actionFrom, @NonNull String actionTo) {
        List<Connection> connections = mConnections.get(actionFrom);
        if (connections == null) return;
        for (Connection connection : connections)
            if (Objects.equals(connection.getIntent().getAction(), actionTo))
                connection.addSkip();
    }

    public static synchronized List<Connection> getConnections(@NonNull String actionFrom) {
        return mConnections.get(actionFrom);
    }

    private static synchronized void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        for (String action : mConnections.keySet())
            filter.addAction(action);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        if (mRegistered) {
            lbm.unregisterReceiver(mInstance);
            mContext.unregisterReceiver(mInstance);
            mRegistered = false;
        }
        lbm.registerReceiver(mInstance, filter);
        mContext.registerReceiver(mInstance, filter);
        mRegistered = true;

        /*IntentFilter globalFilter = new IntentFilter();
        IntentFilter localFilter = new IntentFilter();
        for (String action : mConnections.keySet()) {
            boolean local = false, global = false;
            List<Connection> connections = mConnections.get(action);
            for (Connection connection : connections) {
                switch (connection.getTargetingType()) {
                    case All: local = true;
                    case GLOBAL: global = true; break;
                    case LOCAL: local = true; break;
                }
                if (global && local) break;
            }

            if (global) globalFilter.addAction(action);
            if (local) localFilter.addAction(action);
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        if (mRegistered) {
            lbm.unregisterReceiver(mInstance);
            mContext.unregisterReceiver(mInstance);
            mRegistered = false;
        }
        lbm.registerReceiver(mInstance, localFilter);
        mContext.registerReceiver(mInstance, globalFilter);
        mRegistered = true;*/
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        List<Connection> connections = mConnections.get(intent.getAction());
        if (connections == null) return;

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        for (Connection connection : connections) {
            if (!connection.isEnabled() || connection.skip()) continue;
            Intent intentTo = new Intent(connection.getIntent());
            Bundle extras = intentTo.getExtras();
            intentTo.putExtras(intent);
            if (extras != null) intentTo.putExtras(extras);
            switch (connection.getTargetingType()) {
                case GLOBAL:
                    context.sendBroadcast(intentTo);
                    break;
                case LOCAL:
                    lbm.sendBroadcast(intentTo);
                    break;
                default:
                    Log.e(LOG_TAG, "Detected problem in " + LOG_TAG
                            + ": can't recognise BroadcastTargetingType in " + connection);
                    break;
            }
        }
    }

    public enum BroadcastTargetingType {
        GLOBAL, LOCAL
    }

    public static class Connection {

        private final BroadcastTargetingType targetingType;
        @NonNull private final Intent intent;
        @Nullable private final String groupName;
        private int toSkip = 0;
        private boolean enabled = true;

        public Connection(@NonNull BroadcastTargetingType targetingType, @NonNull String actionTo) {
            this(targetingType, actionTo, (String) null);
        }

        public Connection(@NonNull BroadcastTargetingType targetingType, @NonNull String actionTo,
                          @Nullable String groupName) {
            this(targetingType, actionTo, null, groupName);
        }

        public Connection(@NonNull BroadcastTargetingType targetingType, @NonNull String actionTo,
                          @Nullable Bundle additionalIntentData) {
            this(targetingType, actionTo, additionalIntentData, null);
        }

        public Connection(@NonNull BroadcastTargetingType targetingType, @NonNull String actionTo,
                          @Nullable Bundle additionalIntentData, @Nullable String groupName) {
            this(targetingType, new Intent(actionTo).putExtras(additionalIntentData != null
                    ? additionalIntentData : new Bundle()), groupName);
        }

        public Connection(@NonNull BroadcastTargetingType targetingType, @NonNull Intent intentTo) {
            this(targetingType, intentTo, null);
        }

        public Connection(@NonNull BroadcastTargetingType targetingType, @NonNull Intent intentTo,
                          @Nullable String groupName) {
            this.targetingType = targetingType;
            this.intent = intentTo;
            this.groupName = groupName;
        }

        @Nullable
        public String getGroupName() {
            return groupName;
        }

        @NonNull
        public BroadcastTargetingType getTargetingType() {
            return targetingType;
        }

        @NonNull
        public Intent getIntent() {
            return intent;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void addSkip() {
            toSkip++;
        }

        boolean skip() {
            if (toSkip <= 0) return false;
            toSkip--;
            return true;
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "targetingType=" + targetingType +
                    ", intent=" + intent +
                    ", groupName='" + groupName + '\'' +
                    ", toSkip=" + toSkip +
                    ", enabled=" + enabled +
                    '}';
        }
    }
}

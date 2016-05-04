package eu.codetopic.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anty on 3.5.16.
 *
 * @author anty
 */
public final class BroadcastsConnector extends BroadcastReceiver {

    private static final String LOG_TAG = "BroadcastsConnector";
    private static final BroadcastsConnector mInstance = new BroadcastsConnector();
    private static final HashMap<String, List<Connection>> mConnections = new HashMap<>();
    private static Context mContext = null;
    private static boolean mRegistered = false;

    private BroadcastsConnector() {
    }

    public static synchronized void initialize(@NonNull Context context) {
        if (mContext != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mContext = context.getApplicationContext();
    }

    public static void connect(@NonNull String actionFrom, @NonNull String actionTo) {
        connect(actionFrom, actionTo, null);
    }

    public static void connect(@NonNull String actionFrom, @NonNull String actionTo,
                               @Nullable Bundle additionalIntentData) {
        connect(actionFrom, new Connection(actionTo, additionalIntentData));
    }

    public static void connect(@NonNull String actionFrom, @NonNull Intent intent) {
        connect(actionFrom, new Connection(intent));
    }

    public static synchronized void connect(@NonNull String actionFrom, @NonNull Connection connection) {
        try {
            List<Connection> connections = mConnections.get(actionFrom);
            if (connections == null) {
                connections = new ArrayList<>();
                mConnections.put(actionFrom, connections);
            }
            connections.add(connection);
        } finally {
            registerBroadcast();
        }
    }

    public static synchronized void disconnectAll(@NonNull String actionFrom) {
        try {
            mConnections.remove(actionFrom);
        } finally {
            registerBroadcast();
        }
    }

    private static synchronized void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        for (String action : mConnections.keySet())
            filter.addAction(action);

        if (mRegistered) {
            mContext.unregisterReceiver(mInstance);
            mRegistered = false;
        }
        mContext.registerReceiver(mInstance, filter);
        mRegistered = true;
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        List<Connection> connections = mConnections.get(intent.getAction());
        if (connections == null) return;
        for (Connection connection : connections) {
            Intent intentTo = new Intent(connection.getIntent());
            Bundle extras = intentTo.getExtras();
            intentTo.putExtras(intent);
            if (extras != null) intentTo.putExtras(extras);
            context.sendBroadcast(intentTo);
        }
    }

    public static class Connection {

        private final Intent mIntent;

        public Connection(@NonNull String actionTo) {
            this(actionTo, null);
        }

        public Connection(@NonNull String actionTo, @Nullable Bundle additionalIntentData) {
            this(new Intent(actionTo).putExtras(additionalIntentData != null
                    ? additionalIntentData : new Bundle()));
        }

        public Connection(@NonNull Intent intentTo) {
            mIntent = intentTo;
        }

        public Intent getIntent() {
            return mIntent;
        }
    }
}

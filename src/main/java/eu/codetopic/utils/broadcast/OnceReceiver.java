package eu.codetopic.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;

import eu.codetopic.utils.broadcast.BroadcastsConnector.BroadcastTargetingType;
import eu.codetopic.utils.log.Log;

@UiThread
public abstract class OnceReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "OnceReceiver";

    private final BroadcastTargetingType target;

    public OnceReceiver(BroadcastTargetingType target, Context context, String action) {
        this(target, context, new IntentFilter(action));
    }

    public OnceReceiver(BroadcastTargetingType target, Context context, IntentFilter intentFilter) {
        this.target = target;
        switch (target) {
            case GLOBAL:
                context.registerReceiver(this, intentFilter);
                break;
            case LOCAL:
                LocalBroadcastManager.getInstance(context)
                        .registerReceiver(this, intentFilter);
                break;
            default:
                Log.e(LOG_TAG, "Detected problem in " + LOG_TAG
                        + ": can't recognise BroadcastTargetingType - " + target);
                break;
        }
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        switch (target) {
            case GLOBAL:
                context.unregisterReceiver(this);
                break;
            case LOCAL:
                LocalBroadcastManager.getInstance(context)
                        .unregisterReceiver(this);
                break;
            default:
                Log.e(LOG_TAG, "Detected problem in " + LOG_TAG
                        + ": can't recognise BroadcastTargetingType - " + target);
                break;
        }
        onReceived(context, intent);
    }

    public abstract void onReceived(Context context, Intent intent);
}

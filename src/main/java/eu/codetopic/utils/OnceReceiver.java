package eu.codetopic.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class OnceReceiver extends BroadcastReceiver {

    public OnceReceiver(Context context, String action) {
        this(context, new IntentFilter(action));
    }

    public OnceReceiver(Context context, IntentFilter intentFilter) {
        context.registerReceiver(this, intentFilter);
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        context.unregisterReceiver(this);
        onReceived(context, intent);
    }

    public abstract void onReceived(Context context, Intent intent);
}

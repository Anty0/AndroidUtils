package com.codetopic.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class SkipSupportBroadcastReceiver extends BroadcastReceiver {

    private int toSkip = 0;

    public synchronized void skipNext() {
        toSkip++;
    }

    @Override
    public final synchronized void onReceive(Context context, Intent intent) {
        if (toSkip > 0) {
            toSkip--;
            onDisallowedReceive(context, intent);
            return;
        }
        onAllowedReceive(context, intent);
    }

    public abstract void onAllowedReceive(Context context, Intent intent);

    public void onDisallowedReceive(Context context, Intent intent) {
    }
}

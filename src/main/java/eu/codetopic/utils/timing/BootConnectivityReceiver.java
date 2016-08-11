package eu.codetopic.utils.timing;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootConnectivityReceiver extends BroadcastReceiver {

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent) {
        if (!TimedComponentsManager.isInitialized()) return;
        TimedComponentsManager.getInstance().notifyIntentReceived(intent);
    }
}

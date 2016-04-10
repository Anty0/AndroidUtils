package eu.codetopic.utils.timing;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public class BootConnectivityReceiver extends BroadcastReceiver {

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent) {
        if (!TimedBroadcastsManager.isInitialized()) return;
        TimedBroadcastsManager.getInstance().notifyIntentReceived(intent);
    }
}

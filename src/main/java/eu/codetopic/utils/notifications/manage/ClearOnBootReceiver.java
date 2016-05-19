package eu.codetopic.utils.notifications.manage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.Objects;

public class ClearOnBootReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "ClearOnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) return;
        NotificationIdsManager idsModule = NotificationIdsManager.getInstance();
        if (idsModule == null) {
            Log.d(LOG_TAG, "NotificationIdsManager is not initialized, skipping ids clear");
            return;
        }
        idsModule.getData().clearIds();
    }
}

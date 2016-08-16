package eu.codetopic.utils.notifications.ids;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eu.codetopic.utils.Objects;
import eu.codetopic.utils.log.Log;

public class ClearOnBootReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "ClearOnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) return;
        if (!NotificationCase.isInitialized()) {
            Log.d(LOG_TAG, "NotificationCase is not initialized, skipping ids clear and notifications restoring");
            return;
        }

        for (NotificationCase notification : NotificationsData.getter.get().getNotifications()) {
            if (!notification.isPersistent() || !notification.isShowed())
                notification.cancel(context);
            else notification.update(context);
        }
    }
}

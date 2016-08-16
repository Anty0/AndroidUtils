package eu.codetopic.utils.notifications.ids;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eu.codetopic.utils.log.Log;

public class NotificationDeleteReceiver extends BroadcastReceiver {

    static final String EXTRA_NOTIFICATION_CASE_ID = "eu.codetopic.utils.notifications.ids.NotificationDeleteReceiver.NOTIFICATION_CASE_ID";
    static final String EXTRA_PENDING_INTENT_TO_START = "eu.codetopic.utils.notifications.ids.NotificationDeleteReceiver.PENDING_INTENT";
    private static final String LOG_TAG = "NotificationDeleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!NotificationCase.isInitialized()) {
                Log.e(LOG_TAG, "NotificationCase is not initialized, can't remove notification.");
            } else {
                //noinspection ConstantConditions
                NotificationsData.getter.get().findNotification(intent
                        .getIntExtra(EXTRA_NOTIFICATION_CASE_ID, -1)).notifyCanceled();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "onReceive", e);
        }

        try {
            PendingIntent pIntent = intent.getParcelableExtra(EXTRA_PENDING_INTENT_TO_START);
            if (pIntent != null) pIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.d(LOG_TAG, "onReceive", e);
        }
    }
}

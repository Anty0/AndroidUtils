package eu.codetopic.utils.notifications.manage;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eu.codetopic.utils.Log;

/**
 * Created by anty on 7.3.16.
 *
 * @author anty
 */
public class NotificationDeleteReceiver extends BroadcastReceiver {

    static final String EXTRA_GROUP = "eu.codetopic.utils.notifications.manage.NotificationDeleteReceiver.GROUP";
    static final String EXTRA_NOTIFICATION_ID = "eu.codetopic.utils.notifications.manage.NotificationDeleteReceiver.NOTIFICATION_ID";
    static final String EXTRA_PENDING_INTENT_TO_START = "eu.codetopic.utils.notifications.manage.NotificationDeleteReceiver.PENDING_INTENT";
    private static final String LOG_TAG = "NotificationDeleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            NotificationIdsModule.getInstance()
                    .notifyIdRemoved((Group) intent.getSerializableExtra(EXTRA_GROUP),
                            intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1));
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

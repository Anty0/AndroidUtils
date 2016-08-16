package eu.codetopic.utils.notifications.ids;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import eu.codetopic.utils.ids.RequestCodes;

public final class DeleteIntentExtender implements NotificationCompat.Extender {

    private static final String LOG_TAG = "DeleteIntentExtender";

    private NotificationCase notificationCase = null;
    private PendingIntent deleteIntent = null;

    DeleteIntentExtender setNotificationCase(NotificationCase notificationCase) {
        this.notificationCase = notificationCase;
        return this;
    }

    public DeleteIntentExtender setDeleteIntent(PendingIntent intent) {
        deleteIntent = intent;
        return this;
    }

    @Override
    public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
        if (notificationCase == null || !notificationCase.hasId())
            throw new IllegalStateException(LOG_TAG + " is not have valid notificationCase");

        builder.setDeleteIntent(PendingIntent.getBroadcast(builder.mContext, RequestCodes.requestCode(),
                new Intent(builder.mContext, NotificationDeleteReceiver.class)
                        .putExtra(NotificationDeleteReceiver.EXTRA_NOTIFICATION_CASE_ID, notificationCase.getId())
                        .putExtra(NotificationDeleteReceiver.EXTRA_PENDING_INTENT_TO_START, deleteIntent),
                PendingIntent.FLAG_UPDATE_CURRENT));
        return builder;
    }
}

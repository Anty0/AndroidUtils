package com.codetopic.utils.notifications.manage;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class DeleteIntentExtender implements NotificationCompat.Extender {

    private static final String LOG_TAG = "DeleteIntentExtender";

    private Group group = null;
    private int requestCode = 0;
    private int notificationId = -1;
    private PendingIntent deleteIntent = null;

    DeleteIntentExtender setGroup(Group group) {
        this.group = group;
        return this;
    }

    DeleteIntentExtender setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    DeleteIntentExtender setNotificationId(int id) {
        notificationId = id;
        return this;
    }

    public DeleteIntentExtender setDeleteIntent(PendingIntent intent) {
        deleteIntent = intent;
        return this;
    }

    @Override
    public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
        if (group == null || notificationId == -1)
            throw new IllegalStateException(LOG_TAG + " is not have notificationId or group");
        builder.setDeleteIntent(PendingIntent.getBroadcast(builder.mContext, requestCode,
                new Intent(builder.mContext, NotificationDeleteReceiver.class)
                        .putExtra(NotificationDeleteReceiver.EXTRA_GROUP, group)
                        .putExtra(NotificationDeleteReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                        .putExtra(NotificationDeleteReceiver.EXTRA_PENDING_INTENT_TO_START, deleteIntent),
                PendingIntent.FLAG_UPDATE_CURRENT));
        return builder;
    }
}

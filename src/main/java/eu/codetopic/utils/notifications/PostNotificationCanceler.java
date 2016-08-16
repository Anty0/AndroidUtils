package eu.codetopic.utils.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

import eu.codetopic.utils.ids.RequestCodes;
import eu.codetopic.utils.notifications.ids.NotificationCase;
import eu.codetopic.utils.notifications.ids.NotificationsData;

public class PostNotificationCanceler extends BroadcastReceiver {

    private static final String EXTRA_GROUP = "eu.codetopic.utils.notifications.PostNotificationCanceler.GROUP";
    private static final String EXTRA_NOTIFICATION_ID = "eu.codetopic.utils.notifications.PostNotificationCanceler.NOTIFICATION_ID";

    public static void postNotificationCancel(Context context, int notificationId, long waitTime) {
        postNotificationCancel(context, notificationId, null, waitTime);
    }

    public static void postNotificationCancel(Context context, @NonNull String group, long waitTime) {
        postNotificationCancel(context, -1, group, waitTime);
    }

    private static void postNotificationCancel(Context context, int notificationId, @Nullable String group, long waitTime) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + waitTime,
                        PendingIntent.getBroadcast(context, RequestCodes.requestCode(),
                                new Intent(context, PostNotificationCanceler.class)
                                        .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                                        .putExtra(EXTRA_GROUP, group),
                                PendingIntent.FLAG_CANCEL_CURRENT));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) return;

        int id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1);
        String group = intent.getStringExtra(EXTRA_GROUP);

        if (group != null) {
            List<NotificationCase> notifications = NotificationsData.getter.get().findNotifications(group);
            for (NotificationCase notification : notifications)
                notification.cancel(context);
            return;
        }

        if (id != -1) {
            NotificationCase notification = NotificationsData.getter.get().findNotification(id);
            if (notification != null) {
                notification.cancel(context);
                return;
            }
            NotificationManagerCompat.from(context).cancel(id);
        }
    }
}

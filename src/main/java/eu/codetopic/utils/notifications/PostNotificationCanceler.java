package eu.codetopic.utils.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import eu.codetopic.utils.ids.Identifiers;

public class PostNotificationCanceler extends BroadcastReceiver {

    private static final String EXTRA_NOTIFICATION_ID = "eu.codetopic.utils.notifications.PostNotificationCanceler.NOTIFICATION_ID";

    public static void postNotificationCancel(Context context, int notificationId, long waitTime) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + waitTime,
                        PendingIntent.getBroadcast(context, Identifiers.next(Identifiers.TYPE_REQUEST_CODE),
                                new Intent(context, PostNotificationCanceler.class)
                                        .putExtra(EXTRA_NOTIFICATION_ID, notificationId),
                                PendingIntent.FLAG_CANCEL_CURRENT));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) return;
        NotificationManagerCompat.from(context)
                .cancel(intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1));
    }
}

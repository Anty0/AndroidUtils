package eu.codetopic.utils.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import eu.codetopic.utils.notifications.manage.Group;
import eu.codetopic.utils.notifications.manage.NotificationIdsModule;

/**
 * Created by anty on 14.11.2015.
 *
 * @author anty
 */
public class PostNotificationCanceler extends BroadcastReceiver {

    private static final String DEFAULT_GROUP_NAME = "DEFAULT_GROUP";
    private static final String ACTION_ADD_NAME = "eu.codetopic.utils.notifications.PostNotificationCanceler.";
    private static final String EXTRA_GROUP = "eu.codetopic.utils.notifications.PostNotificationCanceler.GROUP";
    private static final String EXTRA_NOTIFICATION_ID = "eu.codetopic.utils.notifications.PostNotificationCanceler.NOTIFICATION_ID";

    public static void postNotificationCancel(Context context, @Nullable Group group, int notificationId, long waitTime) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + waitTime, PendingIntent
                        .getBroadcast(context, notificationId,
                                new Intent(context, PostNotificationCanceler.class)
                                        .setAction(ACTION_ADD_NAME + (group == null ?
                                                DEFAULT_GROUP_NAME : group.getName()))
                                        .putExtra(EXTRA_GROUP, group)
                                        .putExtra(EXTRA_NOTIFICATION_ID, notificationId),
                                PendingIntent.FLAG_CANCEL_CURRENT));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) return;

        Group group = (Group) intent.getSerializableExtra(EXTRA_GROUP);
        int id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1);
        if (group != null) {
            NotificationIdsModule.getInstance().cancelNotification(group, id);
            return;
        }
        ((NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE)).cancel(id);
    }
}

package eu.codetopic.utils.notifications.ids;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.Serializable;

import eu.codetopic.utils.thread.JobUtils;

public final class NotificationCase implements Serializable {

    private static final String LOG_TAG = "NotificationCase";
    private final long when;
    private final String group;
    private final boolean persistent;
    private final NotificationCreator notificationCreator;
    private int id = -1;
    private boolean showed = false;

    public NotificationCase(@Nullable String group, boolean persistent,
                            @NonNull NotificationCreator notificationCreator) {

        if (!isInitialized()) throw new IllegalStateException(LOG_TAG + " is not initialized");
        this.group = group;
        this.persistent = persistent;
        this.notificationCreator = notificationCreator;
        this.when = System.currentTimeMillis();
    }

    @MainThread
    public static void initialize(Context context) {
        NotificationsData.initialize(context);
    }

    public static boolean isInitialized() {
        return NotificationsData.isInitialized();
    }

    public static void cancelGroup(Context context, String group) {
        for (NotificationCase notification : NotificationsData.getter
                .get().findNotifications(group)) {
            notification.cancel(context);
        }
    }

    public synchronized int getId() {
        return id;
    }

    synchronized void setId(int id) {
        this.id = id;
    }

    public synchronized boolean hasId() {
        return id != -1;
    }

    public synchronized boolean isShowed() {
        return showed;
    }

    public synchronized String getGroup() {
        return group;
    }

    public synchronized boolean isPersistent() {
        return persistent;
    }

    public synchronized NotificationCreator getNotificationCreator() {
        return notificationCreator;
    }

    @MainThread
    public synchronized Notification createNotification(Context context) {
        if (!hasId()) throw new IllegalStateException(LOG_TAG + " hasn't id");
        NotificationCreator notificationCreator = getNotificationCreator();
        NotificationCompat.Builder notificationBuilder = notificationCreator.create(context, this);
        DeleteIntentExtender deleteExtender = notificationCreator.getDeleteExtender(context, this);
        if (deleteExtender == null) deleteExtender = new DeleteIntentExtender();

        return notificationBuilder.setWhen(when).setGroup(group)
                .extend(deleteExtender.setNotificationCase(this)).build();
    }

    public synchronized void update(Context context) {
        if (showed) internalShow(context, getId());
    }

    public synchronized int prepare() {
        return NotificationsData.getter.get().addNotification(this);
    }

    public synchronized int show(Context context) {
        if (showed) return getId();
        int id = prepare();
        internalShow(context, id);
        notifyShowed();
        return id;
    }

    public synchronized void notifyShowed() {
        showed = true;
        NotificationsData.getter.get().saveNotifications();
    }

    private synchronized void internalShow(final Context context, final int id) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                NotificationManagerCompat.from(context).notify(id, createNotification(context));
            }
        });
    }

    private synchronized void internalHide(final Context context, final int id) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                NotificationManagerCompat.from(context).cancel(id);
            }
        });
    }

    public synchronized void notifyHidden() {
        showed = false;
        NotificationsData.getter.get().saveNotifications();
    }

    public synchronized void hide(Context context) {
        if (!showed || !hasId()) return;
        internalHide(context, getId());
        notifyHidden();
    }

    synchronized void notifyCanceled() {
        NotificationsData.getter.get().removeNotification(this);
    }

    public synchronized void cancel(Context context) {
        hide(context);
        notifyCanceled();
    }

    @Override
    public String toString() {
        return "NotificationCase{" +
                "id=" + id +
                ", showed=" + showed +
                ", when=" + when +
                ", group='" + group + '\'' +
                ", persistent=" + persistent +
                ", notificationCreator=" + notificationCreator +
                '}';
    }

    public interface NotificationCreator extends Serializable {

        @MainThread
        NotificationCompat.Builder create(Context context, NotificationCase selfCase);

        @Nullable
        @MainThread
        DeleteIntentExtender getDeleteExtender(Context context, NotificationCase selfCase);
    }

    public static class SimpleNotificationCreator implements NotificationCreator {

        @Override
        @MainThread
        public NotificationCompat.Builder create(Context context, NotificationCase selfCase) {
            return new android.support.v7.app.NotificationCompat.Builder(context);
        }

        @Override
        @Nullable
        @MainThread
        public DeleteIntentExtender getDeleteExtender(Context context, NotificationCase selfCase) {
            return null;
        }
    }
}

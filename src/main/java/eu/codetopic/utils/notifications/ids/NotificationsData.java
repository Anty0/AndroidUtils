package eu.codetopic.utils.notifications.ids;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.MainThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.data.getter.DataGetter;
import eu.codetopic.utils.data.preferences.SharedPreferencesData;
import eu.codetopic.utils.data.preferences.SharedPreferencesGetterAbs;
import eu.codetopic.utils.log.Log;

public final class NotificationsData extends SharedPreferencesData {

    public static final DataGetter<NotificationsData> getter = new Getter();

    private static final String LOG_TAG = "AppData";
    private static final int SAVE_VERSION = 3;

    private static NotificationsData mInstance = null;

    private ArrayList<NotificationCase> notifications = null;

    private NotificationsData(Context context) {
        super(context, PrefNames.FILE_NAME_NOTIFICATIONS_DATA, SAVE_VERSION);
    }

    @MainThread
    public static void initialize(Context context) {
        if (isInitialized()) throw new IllegalStateException(LOG_TAG + " is still initialized");
        context = context.getApplicationContext();
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, ClearOnBootReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        mInstance = new NotificationsData(context);
        mInstance.init();
    }

    public static boolean isInitialized() {
        return mInstance != null;
    }

    @Override
    protected synchronized void onCreate() {
        super.onCreate();
        restoreNotifications();
    }

    private synchronized void restoreNotifications() {
        String data = getPreferences().getString(PrefNames.NOTIFICATIONS_CASES, null);
        if (data != null) {
            try {
                //noinspection unchecked
                notifications = (ArrayList<NotificationCase>) Utils.fromString(data);
            } catch (IOException | ClassNotFoundException e) {
                Log.e(LOG_TAG, "restoreNotifications", e);
                notifications = null;
            }
        }
        if (notifications == null) notifications = new ArrayList<>();

        /*NotificationCase[] cases = GSON.fromJson(getPreferences()
                .getString(PrefNames.NOTIFICATIONS_CASES, null), NotificationCase[].class);
        notifications = cases == null ? new ArrayList<NotificationCase>()
                : new ArrayList<>(Arrays.asList(cases));*/

        //validate restored data
        if (Log.isInDebugMode()) {
            ArrayList<Integer> usedIds = new ArrayList<>();
            for (NotificationCase notification : notifications) {
                int id;
                if (!notification.hasId()) {
                    Log.e(LOG_TAG, "restoreNotifications -> validate", new IllegalStateException(
                            "Notification hasn't id, NotificationCase: " + notification));
                } else if (usedIds.contains(id = notification.getId())) {
                    Log.e(LOG_TAG, "restoreNotifications -> validate", new IllegalStateException(
                            "Found two or more notifications with same id, id: " + id
                                    + ", NotificationCase: " + notification));
                } else usedIds.add(id);

                if (!notification.isShowed()) Log.e(LOG_TAG, "restoreNotifications -> validate",
                        new IllegalStateException("Found hidden prepared notification. " +
                                "If you show notification, then you must cancel it (not hide)."));
            }
        }
    }

    synchronized void saveNotifications() {
        SharedPreferences.Editor editor = edit();
        try {
            editor.putString(PrefNames.NOTIFICATIONS_CASES, Utils.toString(notifications));
        } catch (IOException e) {
            Log.e(LOG_TAG, "saveNotifications", e);
            editor.remove(PrefNames.NOTIFICATIONS_CASES);
        }
        editor.apply();
        /*edit().putString(PrefNames.NOTIFICATIONS_CASES,
                GSON.toJson(getNotifications())).apply();*/
    }

    synchronized int addNotification(NotificationCase notification) {
        if (!notifications.contains(notification)) {
            int id = getAvailableId();
            notification.setId(id);
            notifications.add(notification);
            saveNotifications();
            return id;
        }
        return notification.getId();
    }

    synchronized void removeNotification(NotificationCase notification) {
        if (notifications.remove(notification)) {
            notification.setId(-1);
            saveNotifications();
        }
    }

    private synchronized int getAvailableId() {
        List<NotificationCase> notifications = new ArrayList<>(this.notifications);
        int id = 0;
        boolean contains = true;
        while (contains) {
            id++;
            contains = false;
            for (NotificationCase notification : notifications) {
                if (notification.getId() == id) {
                    contains = true;
                    notifications.remove(notification);
                    break;
                }
            }
        }
        return id;
    }

    public synchronized NotificationCase[] getNotifications() {
        return notifications.toArray(new NotificationCase[notifications.size()]);
    }

    public synchronized NotificationCase findNotification(int id) {
        for (NotificationCase notification : notifications)
            if (notification.getId() == id) return notification;
        return null;
    }

    public synchronized List<NotificationCase> findNotifications(String group) {
        List<NotificationCase> result = new ArrayList<>();
        for (NotificationCase notification : notifications)
            if (notification.getGroup().equals(group)) result.add(notification);
        return result;
    }

    private static final class Getter extends SharedPreferencesGetterAbs<NotificationsData> {

        @Override
        public NotificationsData get() {
            return mInstance;
        }

        @Override
        public Class<NotificationsData> getDataClass() {
            return NotificationsData.class;
        }
    }
}

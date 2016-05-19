package eu.codetopic.utils.notifications.manage;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.exceptions.WrongIdException;

public final class NotificationIdsManager {

    private static final String LOG_TAG = "NotificationIdsManager";

    private static NotificationIdsManager mInstance = null;

    private final Context mContext;
    private final UsedIdsData mData;

    private NotificationIdsManager(Context context) {
        mContext = context;
        mContext.getPackageManager().setComponentEnabledSetting(
                new ComponentName(mContext, ClearOnBootReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        mData = new UsedIdsData(mContext);
        mData.init();
    }

    public static void initialize(Context context) {// TODO: 6.3.16 initialize in ApplicationBase
        if (mInstance != null) throw new IllegalStateException(LOG_TAG + " is still initialized");
        mInstance = new NotificationIdsManager(context.getApplicationContext());
    }

    public static NotificationIdsManager getInstance() {
        return mInstance;
    }

    UsedIdsData getData() {
        return mData;
    }

    /**
     * if you call this method, you must call notifyIdRemoved() if you don't need this id any more
     *
     * @param group group
     * @return new notification id
     * @hide
     */
    public int obtainNewId(Group group) {
        return group.getNewId();
    }

    /**
     * Removes obtained id from usable ids
     *
     * @param group group
     * @param id    to remove
     * @hide
     */
    public void notifyIdRemoved(Group group, int id) {
        group.onIdRemoved(id);
    }

    public int obtainRequestCode() {
        return mData.nextRequestCode();
    }

    private int setup(Group group, NotificationCompat.Builder n, @NonNull DeleteIntentExtender ex) {
        int id = obtainNewId(group);
        n.extend(ex.setGroup(group).setRequestCode(obtainRequestCode()).setNotificationId(id));
        return id;
    }

    public int startServiceForeground(Group group, Service service, NotificationCompat.Builder n) {
        return startServiceForeground(group, service, n, new DeleteIntentExtender());
    }

    public int startServiceForeground(Group group, Service service, NotificationCompat.Builder n,
                                      @NonNull DeleteIntentExtender ex) {
        int id = setup(group, n, ex);
        service.startForeground(id, n.build());
        return id;
    }

    public void stopServiceForeground(Group group, Service service, int id) {
        if (id != SingleIdGroup.NO_ID && !Arrays.contains(group.getIdsFromCache(), id))
            throw new WrongIdException("Provided id is not valid");
        service.stopForeground(true);
        notifyIdRemoved(group, id);
    }

    public int showNotification(Group group, NotificationCompat.Builder n) {
        return showNotification(group, n, new DeleteIntentExtender());
    }

    public int showNotification(Group group, NotificationCompat.Builder n, DeleteIntentExtender ex) {
        int id = setup(group, n, ex);
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(id, n.build());
        return id;
    }

    public void cancelAllNotifications(Group group) {
        NotificationManager manager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        for (int id : group.getIdsFromCache()) {
            manager.cancel(id);
            notifyIdRemoved(group, id);
        }
    }

    public void cancelNotification(Group group, int id) {
        if (id != SingleIdGroup.NO_ID) {
            if (!Arrays.contains(group.getIdsFromCache(), id))
                throw new WrongIdException("Provided id is not valid");
            ((NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE)).cancel(id);
        }
        notifyIdRemoved(group, id);
    }
}

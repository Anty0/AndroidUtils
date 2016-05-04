package eu.codetopic.utils.notifications.manage;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.R;
import eu.codetopic.utils.exceptions.WrongIdException;
import eu.codetopic.utils.module.ModuleImpl;
import eu.codetopic.utils.module.ModulesManager;
import eu.codetopic.utils.module.component.Component;
import eu.codetopic.utils.module.component.ComponentsManager;
import eu.codetopic.utils.module.data.ModuleDataManager;

/**
 * Created by anty on 6.3.16.
 *
 * @author anty
 */
public final class NotificationIdsModule extends ModuleImpl {// TODO: 6.3.16 add to modules in ApplicationBase

    public static NotificationIdsModule getInstance() {
        return ModulesManager.findModule(NotificationIdsModule.class);
    }

    @NonNull
    @Override
    public CharSequence getName() {
        return getText(R.string.module_name_notifications_ids);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, ClearOnBootReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Nullable
    @Override
    protected ModuleDataManager onCreateDataManager() {
        return new ModuleDataManager(new UsedIdsData(this));
    }

    @Nullable
    @Override
    protected ComponentsManager onCreateComponentsManager() {
        return new ComponentsManager(this,
                new Component(new ComponentName(this, ClearOnBootReceiver.class)),
                new Component(new ComponentName(this, NotificationDeleteReceiver.class)));
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
        return findModuleData(UsedIdsData.class).nextRequestCode();
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

    public void stopServiceForeground(Group singleIdGroup, Service service) {
        int[] ids = singleIdGroup.getIdsFromCache();
        if (!singleIdGroup.usesSingleId() || ids.length > 1)
            throw new WrongIdException("Group uses more then single id and is unusable for this method");
        stopServiceForeground(singleIdGroup, service, ids.length > 0 ? ids[0] : SingleIdGroup.NO_ID);
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
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(id, n.build());
        return id;
    }

    public void cancelAllNotifications(Group group) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        for (int id : group.getIdsFromCache()) {
            manager.cancel(id);
            notifyIdRemoved(group, id);
        }
    }

    public void cancelNotification(Group singleIdGroup) {// FIXME: 27.4.16 why don't use cancelAllNotifications()?
        int[] ids = singleIdGroup.getIdsFromCache();
        if (!singleIdGroup.usesSingleId() || ids.length > 1)
            throw new WrongIdException("Group uses more then single id and is unusable for this method");
        cancelNotification(singleIdGroup, ids.length > 0 ? ids[0] : SingleIdGroup.NO_ID);
    }

    public void cancelNotification(Group group, int id) {
        if (id != SingleIdGroup.NO_ID) {
            if (!Arrays.contains(group.getIdsFromCache(), id))
                throw new WrongIdException("Provided id is not valid");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(id);
        }
        notifyIdRemoved(group, id);
    }
}

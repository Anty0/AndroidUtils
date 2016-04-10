package eu.codetopic.utils.notifications.manage;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import eu.codetopic.utils.Arrays;

/**
 * Created by anty on 8.3.16.
 *
 * @author anty
 */
public abstract class Group implements Serializable {

    private static final String LOG_TAG = "Group";

    private String mName;
    private UsedIdsData mData = null;

    public Group(String name) {
        mName = name;
    }

    /**
     * Returns name for saving cache to SharedPreferences
     *
     * @return name for saving cache to SharedPreferences
     */
    @Nullable
    public String getName() {
        return mName;
    }

    public boolean usesSingleId() {
        return false;
    }

    private UsedIdsData getData() {
        if (mData == null) mData = NotificationIdsModule
                .getInstance().findModuleData(UsedIdsData.class);
        return mData;
    }

    protected void addIdToCache(int id) {
        getData().addId(mName, id);
    }

    protected void removeIdFromCache(int id) {
        getData().removeId(mName, id);
    }

    protected int[] getIdsFromCache() {
        return getData().getIds(mName);
    }

    protected int getFirstFreeId() {
        int[] ids = getData().getAllIds();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!Arrays.contains(ids, i))
                return i;
        }
        throw new IndexOutOfBoundsException("All ids is used");
    }

    /**
     * Called before notification will be showed.
     *
     * @return id for new notification
     */
    protected abstract int getNewId();

    /**
     * Called when notification was canceled.
     *
     * @param id id of removed notification
     */
    protected abstract void onIdRemoved(int id);

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(mName);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        mName = (String) in.readObject();
    }

}

package eu.codetopic.utils.notifications.manage;

import android.content.Context;

import com.google.gson.Gson;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.PrefNames;
import eu.codetopic.utils.exceptions.WrongIdException;
import eu.codetopic.utils.module.data.ModuleData;

/**
 * Created by anty on 8.3.16.
 *
 * @author anty
 */
final class UsedIdsData extends ModuleData {

    private static final Gson GSON = new Gson();

    private static final int SAVE_VERSION = 0;

    public UsedIdsData(Context context) {
        super(context, PrefNames.FILE_NAME_USED_IDS_DATA, false, SAVE_VERSION);
    }

    void addId(String groupName, int id) {
        int[] allIds = getAllIds();
        if (Arrays.contains(allIds, id)) throw new WrongIdException("Id is still used");

        int[] groupIds = getIds(groupName);
        edit().putString(groupName + PrefNames.ADD_IDS_FOR_GROUP,
                GSON.toJson(Arrays.contains(groupIds, id) ? groupIds : Arrays.add(groupIds, id)))
                .putString(PrefNames.ALL_USED_IDS, GSON.toJson(Arrays.add(allIds, id)))
                .apply();
    }

    void removeId(String groupName, int id) {
        int[] allIds = getAllIds();
        int[] groupIds = getIds(groupName);
        if (!Arrays.contains(allIds, id) || !Arrays.contains(groupIds, id))
            throw new WrongIdException("Id is not used");
        edit().putString(groupName + PrefNames.ADD_IDS_FOR_GROUP,
                GSON.toJson(Arrays.remove(groupIds, id)))
                .putString(PrefNames.ALL_USED_IDS, GSON.toJson(Arrays.remove(allIds, id)))
                .apply();
    }

    int[] getIds(String groupName) {
        String data = getPreferences().getString(groupName + PrefNames.ADD_IDS_FOR_GROUP, null);
        return data == null ? new int[0] : GSON.fromJson(data, int[].class);
    }

    int[] getAllIds() {
        String data = getPreferences().getString(PrefNames.ALL_USED_IDS, null);
        return data == null ? new int[0] : GSON.fromJson(data, int[].class);
    }

    void clearIds() {
        edit().clear().putInt(PrefNames.LAST_NOTIFICATION_REQUEST_CODE, getLastRequestCode()).apply();
    }

    private int getLastRequestCode() {
        return getPreferences().getInt(PrefNames.LAST_NOTIFICATION_REQUEST_CODE, 0);
    }

    int nextRequestCode() {
        int lastRequestCode = getLastRequestCode();
        int nextRequestCode = lastRequestCode >= Integer.MAX_VALUE ? 0 : lastRequestCode + 1;
        edit().putInt(PrefNames.LAST_NOTIFICATION_REQUEST_CODE, nextRequestCode).apply();
        return nextRequestCode;
    }
}

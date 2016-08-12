package eu.codetopic.utils.data.database;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import eu.codetopic.utils.data.database.holder.DatabaseObjectHolder;
import eu.codetopic.utils.data.database.holder.HoldableDatabaseObjectUtils;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.reflect.field.FoundField;

public class DatabaseObjectChangeDetector<T> {

    private static final String LOG_TAG = "DatabaseObjectChangeDetector";

    private final Context mContext;
    private final String mBroadcastActionChangedName;
    private boolean mEnabled = true;

    public DatabaseObjectChangeDetector(Context context, Class<T> clazz) {
        mContext = context;
        mBroadcastActionChangedName = generateBroadcastActionChanged(clazz);
    }

    public static <T> IntentFilter getIntentFilterObjectChanged(Class<T> dataClass) {
        IntentFilter filter = new IntentFilter();
        for (String action : getIntentFilterObjectChangedActions(dataClass))
            filter.addAction(action);
        return filter;
    }

    public static <T> String[] getIntentFilterObjectChangedActions(Class<T> dataClass) {
        ArrayList<String> actions = new ArrayList<>();
        actions.add(generateBroadcastActionChanged(dataClass));
        for (FoundField foundField : HoldableDatabaseObjectUtils.getHolderFieldsOf(dataClass)) {
            Class<?> fieldType = foundField.getGenericFieldType();
            while (fieldType.isArray()) fieldType = fieldType.getComponentType();

            try {
                String action = generateBroadcastActionChanged(((DatabaseObjectHolder)
                        fieldType.newInstance()).getDaoGetter().getDaoObjectClass());
                if (!actions.contains(action)) actions.add(action);
            } catch (Exception e) {
                Log.e(LOG_TAG, "getIntentFilterObjectChanged for " + dataClass.getName(), e);
            }
        }
        return actions.toArray(new String[actions.size()]);
    }

    public static <T> String generateBroadcastActionChanged(Class<T> clazz) {
        return clazz.getName() + ".ACTION_CHANGED_IN_DATABASE";
    }

    public static void sendChangeBroadcast(Context context, Class<? extends DatabaseObject> clazz) {
        sendChangeBroadcast(context, generateBroadcastActionChanged(clazz));
    }

    private static void sendChangeBroadcast(Context context, String broadcastActionChangedName) {
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(broadcastActionChangedName));
    }

    public void onChange() {
        if (isEnabled()) sendChangeBroadcast();
    }

    protected void sendChangeBroadcast() {
        sendChangeBroadcast(mContext, mBroadcastActionChangedName);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        onChange();
    }
}

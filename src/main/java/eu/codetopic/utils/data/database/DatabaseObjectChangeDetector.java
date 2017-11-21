/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.data.database;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.java.utils.reflect.field.FoundField;
import eu.codetopic.utils.data.database.holder.DatabaseObjectHolder;
import eu.codetopic.utils.data.database.holder.HoldableDatabaseObjectUtils;

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

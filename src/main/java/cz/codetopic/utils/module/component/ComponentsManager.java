package cz.codetopic.utils.module.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import cz.codetopic.utils.module.HashClassesManager;

/**
 * Created by anty on 13.3.16.
 *
 * @author anty
 */
public class ComponentsManager extends HashClassesManager<Component> {

    private static final String ACTION_UPDATE_COMPONENTS =
            "cz.codetopic.utils.module.component.ComponentsManager.UPDATE_COMPONENTS";

    public ComponentsManager(Context context, Component... components) {
        super(components);
        for (Component component : get()) component.init(context);
        context.registerReceiver(new BroadcastReceiver() {
                                     @Override
                                     public void onReceive(Context context, Intent intent) {
                                         update();
                                     }
                                 },
                new IntentFilter(ACTION_UPDATE_COMPONENTS));
        update();
    }

    public static void updateAllComponents(Context context) {
        context.sendBroadcast(new Intent(ACTION_UPDATE_COMPONENTS));
    }

    public void update() {
        for (Component component : get()) component.onUpdate();
    }

}

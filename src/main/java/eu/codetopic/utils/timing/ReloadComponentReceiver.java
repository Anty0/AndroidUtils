package eu.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import eu.codetopic.utils.Objects;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.timing.info.TimCompInfo;

public class ReloadComponentReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "ReloadComponentReceiver";

    private static final String ACTION_RELOAD_COMPONENT =
            "eu.codetopic.utils.timing.TimedComponentsManager.RELOAD_COMPONENT";
    private static final String EXTRA_TIMED_COMPONENT_CLASS_NAME =
            "eu.codetopic.utils.timing.TimedComponentsManager.TIMED_COMPONENT_CLASS_NAME";

    static Intent generateIntent(Context context, @NonNull TimCompInfo componentInfo) {
        return new Intent(context, ReloadComponentReceiver.class)
                .setAction(ACTION_RELOAD_COMPONENT)
                .putExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME,// fixes api 24 class passing trough PendingIntent
                        componentInfo.getComponentClass().getName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), ACTION_RELOAD_COMPONENT)) return;
        if (!TimedComponentsManager.isInitialized()) return;
        Class<?> clazz;
        try {
            clazz = Class.forName(intent.getStringExtra(EXTRA_TIMED_COMPONENT_CLASS_NAME));
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "onReceive: can't find requested class to reload", e);
            return;
        }
        TimedComponentsManager.getInstance().tryReload(clazz);
    }
}

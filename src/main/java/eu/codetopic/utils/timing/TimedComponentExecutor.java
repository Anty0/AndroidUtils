package eu.codetopic.utils.timing;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import eu.codetopic.utils.Log;

public class TimedComponentExecutor extends BroadcastReceiver {

    private static final String LOG_TAG = "TimedComponentExecutor";

    private static final String EXTRA_TIMED_COMPONENT_INFO =
            "eu.codetopic.utils.timing.TimedComponentExecutor.TIMED_COMPONENT_INFO";
    private static final String EXTRA_EXECUTE_EXTRAS =
            "eu.codetopic.utils.timing.TimedComponentExecutor.EXECUTE_EXTRAS";

    static Intent generateIntent(Context context, String action, TimedComponentInfo componentInfo,
                                 @Nullable Bundle executeExtras) {

        return new Intent(context, TimedComponentExecutor.class).setAction(action)
                .putExtra(EXTRA_TIMED_COMPONENT_INFO, componentInfo)
                .putExtra(EXTRA_EXECUTE_EXTRAS, executeExtras);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getBundleExtra(EXTRA_EXECUTE_EXTRAS);
        if (extras == null) extras = new Bundle();

        Class<?> componentClass = ((TimedComponentInfo) intent
                .getSerializableExtra(EXTRA_TIMED_COMPONENT_INFO)).getComponentClass();

        TimingData.getter.get().setLastExecuteTime(componentClass, System.currentTimeMillis());

        // FIXME: 30.5.16 detect component type
        try {
            Intent targetIntent = new Intent(context, componentClass)
                    .setAction(intent.getAction()).putExtras(extras);

            if (BroadcastReceiver.class.isAssignableFrom(componentClass)) {
                context.sendBroadcast(targetIntent);
            } else if (Service.class.isAssignableFrom(componentClass)) {
                context.startService(targetIntent);
            } else if (Activity.class.isAssignableFrom(componentClass)) {
                context.startActivity(targetIntent);
            } else {
                throw new ClassCastException("Unknown component class: " + componentClass.getName());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't execute component: " + componentClass.getName(), e);
        }

        if (TimedComponentsManager.ACTION_FORCED_EXECUTE.equals(intent.getAction()))
            TimedComponentsManager.getInstance().reload(componentClass);
    }
}

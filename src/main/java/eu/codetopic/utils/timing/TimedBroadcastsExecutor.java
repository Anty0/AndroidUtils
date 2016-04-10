package eu.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import eu.codetopic.utils.module.data.ModuleDataGetter;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public class TimedBroadcastsExecutor extends BroadcastReceiver {

    private static final String EXTRA_TIMING_DATA_GETTER =
            "eu.codetopic.utils.timing.TimedBroadcastsExecutor.TIMING_DATA_GETTER";
    private static final String EXTRA_TIMED_BROADCAST_INFO =
            "eu.codetopic.utils.timing.TimedBroadcastsExecutor.TIMED_BROADCAST_INFO";
    private static final String EXTRA_EXECUTE_EXTRAS =
            "eu.codetopic.utils.timing.TimedBroadcastsExecutor.EXECUTE_EXTRAS";

    static Intent generateIntent(Context context, String action,
                                 ModuleDataGetter<?, TimingData> timingDataGetter,
                                 TimedBroadcastInfo broadcastInfo, Bundle executeExtras) {
        return new Intent(context, TimedBroadcastsExecutor.class).setAction(action)
                .putExtra(EXTRA_TIMING_DATA_GETTER, timingDataGetter)
                .putExtra(EXTRA_TIMED_BROADCAST_INFO, broadcastInfo)
                .putExtra(EXTRA_EXECUTE_EXTRAS, executeExtras);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getBundleExtra(EXTRA_EXECUTE_EXTRAS);
        if (extras == null) extras = new Bundle();

        Class<? extends BroadcastReceiver> broadcastClass = ((TimedBroadcastInfo)
                intent.getSerializableExtra(EXTRA_TIMED_BROADCAST_INFO)).getBroadcastClass();
        //noinspection unchecked
        ((ModuleDataGetter<?, TimingData>)
                intent.getSerializableExtra(EXTRA_TIMING_DATA_GETTER)).get()
                .setLastExecuteTime(broadcastClass, System.currentTimeMillis());
        context.sendBroadcast(new Intent(context, broadcastClass)
                .setAction(intent.getAction()).putExtras(extras));
    }
}

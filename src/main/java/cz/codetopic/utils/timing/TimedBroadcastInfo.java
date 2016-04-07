package cz.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;

import java.io.Serializable;

import cz.codetopic.utils.exceptions.InvalidClass;
import cz.codetopic.utils.exceptions.NoAnnotationException;

/**
 * Created by anty on 24.3.16.
 *
 * @author anty
 */
public class TimedBroadcastInfo implements Serializable {

    private final Class<? extends BroadcastReceiver> mBroadcast;
    private final TimedBroadcast mBroadcastInfo;

    TimedBroadcastInfo(Class broadcastClass) {
        if (!BroadcastReceiver.class.isAssignableFrom(broadcastClass))
            throw new InvalidClass(broadcastClass.getName() + " must extend from BroadcastReceiver");
        //noinspection unchecked
        mBroadcast = broadcastClass;
        mBroadcastInfo = (TimedBroadcast) broadcastClass.getAnnotation(TimedBroadcast.class);
        if (mBroadcastInfo == null)
            throw new NoAnnotationException("TimedBroadcast annotation is not present in "
                    + broadcastClass.getName());
    }

    public Class<? extends BroadcastReceiver> getBroadcastClass() {
        return mBroadcast;
    }

    public int getComponentEnabledState(Context context) {
        return context.getPackageManager().getComponentEnabledSetting(new ComponentName(context, mBroadcast));
    }

    public TimedBroadcast getBroadcastInfo() {
        return mBroadcastInfo;
    }
}

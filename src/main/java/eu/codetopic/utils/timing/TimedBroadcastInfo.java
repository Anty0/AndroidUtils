package eu.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.Context;

import java.io.Serializable;

import eu.codetopic.utils.Utils;
import eu.codetopic.utils.exceptions.InvalidClass;
import eu.codetopic.utils.exceptions.NoAnnotationPresentException;

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
            throw new NoAnnotationPresentException("TimedBroadcast annotation is not present in "
                    + broadcastClass.getName());
    }

    public Class<? extends BroadcastReceiver> getBroadcastClass() {
        return mBroadcast;
    }

    public boolean isEnabled(Context context) {
        return Utils.isComponentEnabled(context, mBroadcast);
    }

    public TimedBroadcast getBroadcastInfo() {
        return mBroadcastInfo;
    }
}

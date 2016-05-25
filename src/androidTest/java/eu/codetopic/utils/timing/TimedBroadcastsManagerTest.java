package eu.codetopic.utils.timing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Toast;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.notifications.manage.NotificationIdsManager;
import eu.codetopic.utils.thread.JobUtils;

@RunWith(AndroidJUnit4.class)
public class TimedBroadcastsManagerTest {

    @Before
    public void setUp() throws Exception {
        NotificationIdsManager.initialize(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testIt() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        TimedBroadcastsManager.initialize(context, NetworkManager
                .NetworkType.ANY, TestTimedBroadcast.class);
        TimedBroadcastsManager.getInstance()
                .setBroadcastEnabled(TestTimedBroadcast.class, true);

        JobUtils.threadSleep(60000);

        TimedBroadcastsManager.getInstance()
                .setBroadcastEnabled(TestTimedBroadcast.class, false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @TimedBroadcast(time = 1000, repeatingMode = TimedBroadcast.RepeatingMode.REPEATING_WAKE_UP)
    public static class TestTimedBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Test toast", Toast.LENGTH_LONG).show();
        }
    }
}
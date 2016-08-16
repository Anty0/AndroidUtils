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
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.timing.info.TimedComponent;

@RunWith(AndroidJUnit4.class)
public class TimedComponentsManagerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testIt() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        TimedComponentsManager.initialize(context, NetworkManager
                .NetworkType.ANY, TestTimedBroadcast.class);
        TimedComponentsManager.getInstance()
                .setComponentEnabled(TestTimedBroadcast.class, true);

        JobUtils.threadSleep(60000);

        TimedComponentsManager.getInstance()
                .setComponentEnabled(TestTimedBroadcast.class, false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @TimedComponent(repeatTime = 1000, repeatingMode = TimedComponent.RepeatingMode.REPEATING_WAKE_UP)
    public static class TestTimedBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Test toast", Toast.LENGTH_LONG).show();
        }
    }
}
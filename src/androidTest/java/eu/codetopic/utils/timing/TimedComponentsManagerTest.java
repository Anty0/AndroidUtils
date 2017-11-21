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

    @TimedComponent(repeatTime = 1000)
    public static class TestTimedBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Test toast", Toast.LENGTH_LONG).show();
        }
    }
}
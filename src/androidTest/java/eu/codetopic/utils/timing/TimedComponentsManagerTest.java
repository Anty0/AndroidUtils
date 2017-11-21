/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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
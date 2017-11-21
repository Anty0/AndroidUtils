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

package eu.codetopic.utils.thread.service;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.NotificationCompat;

import com.birbit.android.jobqueue.Params;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.NetworkManager;
import eu.codetopic.utils.R;
import eu.codetopic.utils.broadcast.BroadcastsConnector;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.SingletonJobManager;
import eu.codetopic.utils.thread.progress.ProgressReporter;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BackgroundWorksServiceTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        NetworkManager.init(context);
        JobUtils.initialize(context);
        BroadcastsConnector.initialize(context);
    }

    @Test
    public void testWithBackgroundWorksService() throws TimeoutException, InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();
        BackgroundWorksService.WorkBinder binder = (BackgroundWorksService.WorkBinder)
                mServiceRule.bindService(new Intent(context, BackgroundWorksService.class));

        SingletonJobManager.initialize(context);

        for (int i = 1; i < 51; i++) {
            binder.startWork(SingletonJobManager.getter, new TestWork(i, 20));
        }

        binder.startWork(SingletonJobManager.getter, new TestWork(1, 30));
        binder.startWork(SingletonJobManager.getter, new TestWork(2, 30));
        binder.startWork(SingletonJobManager.getter, new TestWork(3, 30));
        binder.startWork(SingletonJobManager.getter, new TestWork(4, 30));

        assertTrue(binder.isRunning());
        while (binder.isRunning()) Thread.sleep(500);
        assertTrue(binder.isStopped());
    }

    @After
    public void tearDown() throws Exception {

    }

    @ServiceWork.UseProgress
    @ServiceWork.RetryLimit(0)
    private static class TestWork implements ServiceWork {

        private static final String LOG_TAG = "TestWork";

        private final int index;
        private final int duration;

        TestWork(int index, int duration) {
            this.index = index;
            this.duration = duration;
        }

        @Override
        public Params getJobParams(Context context) {
            return new Params(10);
        }

        @Override
        public NotificationCompat.Extender getNotificationExtender(Context context) {
            Log.d(LOG_TAG, "getNotificationExtender of Test" + index);
            return new NotificationCompat.Extender() {
                @Override
                public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                    return builder.setContentTitle("Test" + index)
                            .setContentText("Test running")
                            .setSmallIcon(R.drawable.ic_action_refresh);
                }
            };
        }

        @Override
        public void run(Context context, ProgressReporter reporter) throws Throwable {
            Log.d(LOG_TAG, "started Test" + index);

            reporter.startShowingProgress();
            reporter.reportProgress(0);
            reporter.setMaxProgress(duration);

            for (int i = 0; i < duration; i++) {
                reporter.reportProgress(i);
                Thread.sleep(100);
            }

            Log.d(LOG_TAG, "ended Test" + index);
        }
    }
}
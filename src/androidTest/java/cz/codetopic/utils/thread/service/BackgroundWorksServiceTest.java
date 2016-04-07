package cz.codetopic.utils.thread.service;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.NotificationCompat;

import com.path.android.jobqueue.Params;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;
import cz.codetopic.utils.module.ModulesManager;
import cz.codetopic.utils.notifications.manage.NotificationIdsModule;
import cz.codetopic.utils.thread.JobUtils;
import cz.codetopic.utils.thread.ProgressReporter;

import static org.junit.Assert.assertTrue;

/**
 * Created by anty on 1.4.16.
 *
 * @author anty
 */
@RunWith(AndroidJUnit4.class)
public class BackgroundWorksServiceTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Before
    public void setUp() throws Exception {
        JobUtils.initialize(InstrumentationRegistry.getTargetContext());
        ModulesManager.initialize(new ModulesManager
                .Configuration(InstrumentationRegistry.getTargetContext())
                .addModules(TestModule.class, NotificationIdsModule.class)
                .setDefaultTheme(R.style.Theme_AppCompat));
    }

    @Test
    public void testWithBackgroundWorksService() throws TimeoutException, InterruptedException {
        BackgroundWorksService.WorkBinder binder = (BackgroundWorksService.WorkBinder)
                mServiceRule.bindService(new Intent(InstrumentationRegistry
                        .getTargetContext(), BackgroundWorksService.class));

        for (int i = 1; i < 51; i++) {
            binder.startWork(TestModule.class, new TestWork(i, 20));
        }

        /*binder.startWork(TestModule.class, new TestWork(1, 30));
        binder.startWork(TestModule.class, new TestWork(2, 30));
        binder.startWork(TestModule.class, new TestWork(3, 30));
        binder.startWork(TestModule.class, new TestWork(4, 30));*/

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
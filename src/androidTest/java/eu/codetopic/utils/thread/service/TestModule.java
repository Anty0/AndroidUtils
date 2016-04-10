package eu.codetopic.utils.thread.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import eu.codetopic.utils.module.ModuleImpl;

/**
 * Created by anty on 1.4.16.
 *
 * @author anty
 */
public class TestModule extends ModuleImpl {

    private static final String LOG_TAG = "TestModule";

    @NonNull
    @Override
    public CharSequence getName() {
        return LOG_TAG;
    }

    @Nullable
    @Override
    protected JobManager onCreateJobManager() {
        return new JobManager(this, new Configuration.Builder(this)
                .id(getName() + "Jobs")
                .minConsumerCount(2)
                .maxConsumerCount(2)
                .build());
    }
}

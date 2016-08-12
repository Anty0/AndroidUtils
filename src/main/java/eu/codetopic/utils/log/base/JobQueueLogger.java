package eu.codetopic.utils.log.base;

import com.birbit.android.jobqueue.log.CustomLogger;

import java.util.Locale;

import eu.codetopic.utils.log.Log;

public final class JobQueueLogger implements CustomLogger {

    private static final String LOG_TAG = "JobQueue";

    @Override
    public boolean isDebugEnabled() {
        return Log.isInDebugMode();
    }

    @Override
    public void e(Throwable t, String text, Object... args) {
        Log.e(LOG_TAG, String.format(Locale.ENGLISH, text, args), t);
    }

    @Override
    public void e(String text, Object... args) {
        Log.e(LOG_TAG, String.format(Locale.ENGLISH, text, args));
    }

    @Override
    public void d(String text, Object... args) {
        Log.d(LOG_TAG, String.format(Locale.ENGLISH, text, args));
    }

    @Override
    public void v(String text, Object... args) {
        Log.v(LOG_TAG, String.format(Locale.ENGLISH, text, args));
    }
}
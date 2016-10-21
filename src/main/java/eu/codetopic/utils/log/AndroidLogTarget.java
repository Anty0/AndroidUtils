package eu.codetopic.utils.log;

import android.util.Log;

import eu.codetopic.java.utils.log.base.LogLine;
import eu.codetopic.java.utils.log.base.LogTarget;
import eu.codetopic.java.utils.log.base.Priority;

public class AndroidLogTarget implements LogTarget {

    @Override
    public void println(LogLine logLine) {
        //noinspection WrongConstant
        android.util.Log.println(getPriorityId(logLine.getPriority()),
                logLine.getTag(), logLine.getMsgWithTr());
    }

    private int getPriorityId(Priority priority) {
        switch (priority) {
            case ASSERT:
                return Log.ASSERT;
            case DEBUG:
                return Log.DEBUG;
            case ERROR:
                return Log.ERROR;
            case INFO:
                return Log.INFO;
            case VERBOSE:
                return Log.VERBOSE;
            case WARN:
                return Log.WARN;
        }
        return Log.ERROR;
    }
}

package eu.codetopic.utils.log.base;

public class DefaultTarget implements LogTarget {

    @Override
    public void println(LogLine logLine) {
        //noinspection WrongConstant
        android.util.Log.println(logLine.getPriority().getSystemId(),
                logLine.getTag(), logLine.getMsgWithTr());
    }
}
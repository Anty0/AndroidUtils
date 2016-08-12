package eu.codetopic.utils.log.base;

public class DefaultTarget implements LogTarget {

    @Override
    public void println(Priority priority, String tag, String msg) {
        //noinspection WrongConstant
        android.util.Log.println(priority.getSystemId(), tag, msg);
    }
}
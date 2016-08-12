package eu.codetopic.utils.log.base;

public interface LogTarget {

    void println(Priority priority, String tag, String msg);
}

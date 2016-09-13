package eu.codetopic.utils.log.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;

public final class LogLine implements Serializable {

    private static final String LOG_TAG = "LogLine";
    @NonNull private final Priority priority;
    @NonNull private final String tag;
    @Nullable private final String msg;
    @Nullable private final Throwable tr;

    /**
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message.  It usually identifies
     *                 the class or activity where the log call occurs.
     * @param msg      The message you would like logged.
     * @param tr       Throwable to be added after your message
     */
    public LogLine(@NonNull Priority priority, @NonNull String tag,
                   @Nullable String msg, @Nullable Throwable tr) {
        this.priority = priority;
        this.tag = tag;
        this.msg = msg;
        this.tr = tr;
    }

    public LogLine(@NonNull Priority priority, @NonNull String tag, @Nullable String msg) {
        this(priority, tag, msg, null);
    }

    @NonNull
    public Priority getPriority() {
        return priority;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    @Nullable
    public String getMsg() {
        return msg;
    }

    @Nullable
    public Throwable getTr() {
        return tr;
    }

    @NonNull
    public String getMsgWithTr() {
        StringBuilder sb = new StringBuilder();
        if (msg != null) sb.append(msg);
        if (tr != null) {
            if (sb.length() > 0) sb.append('\n');
            sb.append(Log.getStackTraceString(tr));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean showTr) {
        return priority.getDisaplayID() + "/" + tag + ": " + (showTr ? getMsgWithTr() : getMsg());
    }
}

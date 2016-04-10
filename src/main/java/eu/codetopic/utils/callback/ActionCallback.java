package eu.codetopic.utils.callback;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

/**
 * Created by anty on 9.4.16.
 *
 * @author anty
 */
public interface ActionCallback<R> {

    @MainThread
    void onActionCompleted(@Nullable R result, @Nullable Throwable caughtThrowable);
}

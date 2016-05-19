package eu.codetopic.utils.callback;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

public interface ActionCallback<R> {

    @MainThread
    void onActionCompleted(@Nullable R result, @Nullable Throwable caughtThrowable);
}

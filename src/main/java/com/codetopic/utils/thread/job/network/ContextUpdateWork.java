package com.codetopic.utils.thread.job.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

public abstract class ContextUpdateWork<C extends Context, D> extends ContextDataWork<C, D> {

    public ContextUpdateWork(@NonNull C context) {
        super(context);
    }

    @Override
    public final void result(@Nullable D data, @Nullable Throwable tr) {
        if (tr != null) error(tr);
        else update(data);
    }

    @UiThread
    public void update(D data) {
        C context = getContext();
        if (context != null) updateContext(context, data);
        else updateNoContext(data);
    }

    @UiThread
    public void updateContext(@NonNull C context, D data) {

    }

    @UiThread
    public void updateNoContext(D data) {

    }

    @UiThread
    public void error(Throwable tr) {
        C context = getContext();
        if (context != null) errorContext(context, tr);
        else errorNoContext(tr);
    }

    @UiThread
    public void errorContext(@NonNull C context, Throwable tr) {

    }

    @UiThread
    public void errorNoContext(Throwable tr) {

    }
}

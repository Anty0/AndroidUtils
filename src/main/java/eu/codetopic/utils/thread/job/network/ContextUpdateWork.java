/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.thread.job.network;

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

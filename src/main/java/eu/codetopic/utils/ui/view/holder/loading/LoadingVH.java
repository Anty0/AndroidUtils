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

package eu.codetopic.utils.ui.view.holder.loading;

import android.content.Context;
import android.support.annotation.UiThread;
import android.view.View;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.ui.view.holder.ViewHolder;

public abstract class LoadingVH extends ViewHolder {

    private static final String LOG_TAG = "LoadingVH";

    private int loadingDepth = 0;

    @UiThread
    @Override
    protected void onViewUpdated() {
        super.onViewUpdated();

        if (loadingDepth == 0) doHideLoading();
        else doShowLoading();
    }

    private Context getViewContext() {
        View view = getView();
        return view == null ? null : view.getContext();
    }

    public final void showLoading() {
        synchronized (getViewLock()) {
            if (loadingDepth == 0) {
                JobUtils.postOnContextThread(getViewContext(), new Runnable() {
                    @Override
                    public void run() {
                        synchronized (getViewLock()) {
                            doShowLoading();
                        }
                    }
                });
            }
            loadingDepth++;
        }
    }

    @UiThread
    protected abstract void doShowLoading();

    public final void hideLoading() {
        synchronized (getViewLock()) {
            loadingDepth--;
            if (loadingDepth == 0) {
                JobUtils.postOnContextThread(getViewContext(), new Runnable() {
                    @Override
                    public void run() {
                        synchronized (getViewLock()) {
                            doHideLoading();
                        }
                    }
                });
            }
            if (loadingDepth < 0)
                Log.e(LOG_TAG, "hideLoading: Called hideLoading()" +
                        " without calling showLoading() before.");
        }
    }

    @UiThread
    protected abstract void doHideLoading();

    public final boolean isLoadingShowed() {
        synchronized (getViewLock()) {
            return loadingDepth > 0;
        }
    }
}

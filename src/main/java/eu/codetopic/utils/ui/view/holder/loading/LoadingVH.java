/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.ui.view.holder.loading;

import android.content.Context;
import android.support.annotation.UiThread;
import android.view.View;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.thread.LooperUtils;
import eu.codetopic.utils.ui.view.holder.ViewHolder;
import kotlin.Unit;

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
                LooperUtils.postOnContextThread(getViewContext(), () -> {
                    synchronized (getViewLock()) {
                        doShowLoading();
                    }
                    return Unit.INSTANCE;
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
                LooperUtils.postOnContextThread(getViewContext(), () -> {
                    synchronized (getViewLock()) {
                        doHideLoading();
                    }
                    return Unit.INSTANCE;
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

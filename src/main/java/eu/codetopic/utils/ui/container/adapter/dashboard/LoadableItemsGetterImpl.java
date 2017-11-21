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

package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.callback.ActionCallback;

public abstract class LoadableItemsGetterImpl implements LoadableItemsGetter {

    private static final String LOG_TAG = "LoadableItemsGetterImpl";

    private boolean loadingStarted = false;
    private Collection<? extends ItemInfo> items = null;

    @Nullable
    protected ItemInfo generateExceptionItem(Context context) {
        return null;
    }

    @UiThread
    @Override
    public synchronized final void reload(Context context) {
        if (loadingStarted) return;

        loadingStarted = true;
        final WeakReference<Context> contextRef = new WeakReference<>(context);
        loadItems(context, new ActionCallback<Collection<? extends ItemInfo>>() {

            boolean called = false;

            @Override
            public void onActionCompleted(@Nullable Collection<? extends ItemInfo> result,
                                          @Nullable Throwable caughtThrowable) {
                if (caughtThrowable != null)
                    Log.d(LOG_TAG, "getItems -> loadItems", caughtThrowable);

                if (called) {
                    Log.e(LOG_TAG, "getItems -> loadItems: " +
                            "onActionCompleted was called more then one time!");
                    return;
                }
                called = true;

                Context context = contextRef.get();
                if (result != null) {
                    items = result;
                } else {
                    if (context != null) {
                        ItemInfo exceptionItem = generateExceptionItem(context);
                        items = exceptionItem != null
                                ? Collections.singleton(exceptionItem)
                                : Collections.<ItemInfo>emptySet();
                    } else items = Collections.emptySet();
                }
                if (context != null)
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(new Intent(DashboardAdapter.ACTION_ITEMS_CHANGED));


                loadingStarted = false;
            }
        });
    }

    @UiThread
    @Override
    public boolean isLoaded(Context context) {
        return items != null;
    }

    @UiThread
    protected abstract void loadItems(Context context, ActionCallback<Collection<? extends ItemInfo>> callback);

    @NonNull
    @UiThread
    @Override
    public final Collection<? extends ItemInfo> getItems(Context context) {
        if (items == null) {
            reload(context);
            return items == null ? Collections.<ItemInfo>emptySet() : items;
        }
        return items;
    }
}

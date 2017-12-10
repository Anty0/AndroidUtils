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
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper;
import eu.codetopic.utils.ui.container.items.custom.EmptyCustomItem;
import eu.codetopic.utils.ui.container.items.custom.LoadingItem;

public abstract class LoadableItemInfo extends ItemInfo { // TODO: use replace Callback with kotlin's lambda type

    private static final String LOG_TAG = "LoadableItemInfo";

    private boolean loadingStarted = false;
    private CustomItem loadingItem = null;
    private CustomItem item = null;

    public abstract CharSequence getLoadingName(Context context);

    @UiThread
    protected abstract void loadItem(Context context, Callback<CustomItem> callback);

    @NonNull
    private CustomItem getLoadingItem(Context context) {
        if (loadingItem == null)
            loadingItem = generateLoadingItem(context);

        return loadingItem;
    }

    @NonNull
    protected CustomItem generateLoadingItem(Context context) {
        return new LoadingItem(getLoadingName(context), context.getText(R.string.wait_text_loading)) {
            @NonNull
            @Override
            protected CustomItemWrapper[] getWrappers(Context context) {
                return CardViewWrapper.WRAPPER;
            }
        };
    }

    @Nullable
    protected CustomItem generateExceptionItem(Context context) {
        return null;
    }

    @NonNull
    @Override
    public synchronized final CustomItem getItem(Context context) {
        if (item == null) {
            if (!loadingStarted) {
                loadingStarted = true;
                final WeakReference<Context> contextRef = new WeakReference<>(context);
                loadItem(context, (result, caughtThrowable) -> {
                    if (caughtThrowable != null)
                        Log.d(LOG_TAG, "getItems -> loadItem", caughtThrowable);

                    if (item != null) {
                        Log.e(LOG_TAG, "getItems -> loadItem: " +
                                "onActionCompleted was called more then one time!");
                        return;
                    }

                    Context context1 = contextRef.get();
                    if (context1 != null) {
                        if (result != null) {
                            item = result;
                        } else {
                            CustomItem exceptionItem = generateExceptionItem(context1);
                            item = exceptionItem != null ? exceptionItem
                                    : new EmptyCustomItem();
                        }

                        LocalBroadcastManager.getInstance(context1)
                                .sendBroadcast(new Intent(DashboardAdapter.ACTION_ITEMS_CHANGED));
                    } else item = new EmptyCustomItem();
                });
            }
            return getLoadingItem(context);
        }
        return item;
    }

    public interface Callback<R> {

        @MainThread
        void onCompleted(@Nullable R result, @Nullable Throwable caughtThrowable);
    }
}

package eu.codetopic.utils.container.adapter.dashboard;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.container.items.custom.CustomItem;
import eu.codetopic.utils.container.items.custom.EmptyCustomItem;
import eu.codetopic.utils.container.items.custom.MultilineItemCustomItemWrapper;
import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.TextMultilineResourceLayoutItem;

public abstract class LoadableItemInfo extends ItemInfo {

    private static final String LOG_TAG = "LoadableItemInfo";

    private boolean loadingStarted = false;
    private CustomItem loadingItem = null;
    private CustomItem item = null;

    public abstract CharSequence getLoadingName(Context context);

    @UiThread
    protected abstract void loadItem(Context context, ActionCallback<CustomItem> callback);

    @NonNull
    private CustomItem getLoadingItem(Context context) {
        if (loadingItem == null)
            loadingItem = generateLoadingItem(context);

        return loadingItem;
    }

    @NonNull
    protected CustomItem generateLoadingItem(Context context) {
        MultilineItem item = new TextMultilineResourceLayoutItem(getLoadingName(context),
                context.getText(R.string.wait_text_loading), R.layout.item_multiline_loading);
        return new MultilineItemCustomItemWrapper(item, new CardViewWrapper());
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
                loadItem(context, new ActionCallback<CustomItem>() {
                    @Override
                    public void onActionCompleted(@Nullable CustomItem result,
                                                  @Nullable Throwable caughtThrowable) {
                        if (caughtThrowable != null)
                            Log.d(LOG_TAG, "getItems -> loadItem", caughtThrowable);

                        if (item != null) {
                            Log.e(LOG_TAG, "getItems -> loadItem: " +
                                    "onActionCompleted was called more then one time!");
                            return;
                        }

                        Context context = contextRef.get();
                        if (context != null) {
                            if (result != null) {
                                item = result;
                            } else {
                                CustomItem exceptionItem = generateExceptionItem(context);
                                item = exceptionItem != null ? exceptionItem
                                        : new EmptyCustomItem();
                            }

                            LocalBroadcastManager.getInstance(context)
                                    .sendBroadcast(new Intent(DashboardAdapter.ACTION_ITEMS_CHANGED));
                        } else item = new EmptyCustomItem();
                    }
                });
            }
            return getLoadingItem(context);
        }
        return item;
    }
}

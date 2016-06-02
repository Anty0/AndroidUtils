package eu.codetopic.utils.container.adapter.dashboard;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.container.items.custom.CustomItem;
import eu.codetopic.utils.container.items.custom.MultilineItemCustomItemWrapper;
import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.TextMultilineResourceLayoutItem;

public abstract class LoadableItemsGetter implements ItemsGetter, ReloadableItemsGetter {

    private static final String LOG_TAG = "LoadableItemsGetter";

    private boolean loadingStarted = false;
    private ItemInfo loadingItem = null;
    private Collection<? extends ItemInfo> items = null;

    public abstract CharSequence getLoadingName(Context context);

    @UiThread
    protected abstract void loadItems(Context context, ActionCallback<Collection<? extends ItemInfo>> callback);

    @NonNull
    private ItemInfo getLoadingItem(Context context) {
        if (loadingItem == null)
            loadingItem = new SimpleItemInfo(generateLoadingItem(context),
                    getLoadingItemPriority());

        return loadingItem;
    }

    protected int getLoadingItemPriority() {
        return 0;
    }

    @NonNull
    protected CustomItem generateLoadingItem(Context context) {
        MultilineItem item = new TextMultilineResourceLayoutItem(getLoadingName(context),
                context.getText(R.string.wait_text_loading), R.layout.item_multiline_loading);
        return new MultilineItemCustomItemWrapper(item, new CardViewWrapper());
    }

    @Nullable
    protected ItemInfo generateExceptionItem(Context context) {
        return null;
    }

    @UiThread
    @Override
    public void reload(Context context) {
        if (loadingStarted) return;
        items = null;
        getItems(context);
    }

    @NonNull
    @UiThread
    @Override
    public synchronized final Collection<? extends ItemInfo> getItems(Context context) {
        if (items == null) {
            if (!loadingStarted) {
                loadingStarted = true;
                final WeakReference<Context> contextRef = new WeakReference<>(context);
                loadItems(context, new ActionCallback<Collection<? extends ItemInfo>>() {
                    @Override
                    public void onActionCompleted(@Nullable Collection<? extends ItemInfo> result,
                                                  @Nullable Throwable caughtThrowable) {
                        if (caughtThrowable != null)
                            Log.d(LOG_TAG, "getItems -> loadItems", caughtThrowable);

                        if (items != null) {
                            Log.e(LOG_TAG, "getItems -> loadItems: " +
                                    "onActionCompleted was called more then one time!");
                            return;
                        }

                        Context context = contextRef.get();
                        if (context != null) {
                            if (result != null) {
                                items = result;
                            } else {
                                ItemInfo exceptionItem = generateExceptionItem(context);
                                items = exceptionItem != null
                                        ? Collections.singleton(exceptionItem)
                                        : Collections.<ItemInfo>emptySet();
                            }

                            context.sendBroadcast(new Intent(DashboardAdapter.ACTION_ITEMS_CHANGED));
                        } else items = Collections.emptySet();

                        loadingStarted = false;
                    }
                });
            }
            return Collections.singleton(getLoadingItem(context));
        }
        return items;
    }
}

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

package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.codetopic.utils.ui.container.adapter.ArrayEditAdapter;
import eu.codetopic.utils.ui.container.adapter.UniversalAdapter;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

public class DashboardAdapter extends ArrayEditAdapter<ItemInfo, UniversalAdapter.ViewHolder> {

    public static final String ACTION_ITEMS_CHANGED =
            "DashboardAdapter.ITEMS_CHANGED";
    public static final String ACTION_RELOAD_ITEM =
            "DashboardAdapter.RELOAD_ITEM";
    public static final String EXTRA_CLASS_OF_ITEM_TO_RELOAD =
            "DashboardAdapter.CLASS_OF_ITEM_TO_RELOAD";

    private static final String LOG_TAG = "DashboardAdapter";
    private static final Object EDIT_TAG = new Object();//LOG_TAG + ".EDIT_TAG";

    private final Context mContext;
    private final DashboardItemsFilter mFilter;
    private final ItemsGetter[] mItemsGetters;
    private final LoadingVH mLoadingHolder;
    private boolean mLoadingShowed;
    private final BroadcastReceiver mItemsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyItemsChanged();
        }
    };
    private final BroadcastReceiver mReloadItemsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //noinspection unchecked
            Class<? extends ItemsGetter> classToReload = (Class<? extends ItemsGetter>) intent
                    .getSerializableExtra(EXTRA_CLASS_OF_ITEM_TO_RELOAD);

            if (classToReload == null) notifyReloadItems();
            else notifyReloadItem(classToReload);
        }
    };
    private boolean mActivated = false;

    public DashboardAdapter(@NonNull Context context, LoadingVH loadingHolder,
                            ItemsGetter... itemsGetters) {
        this(context, loadingHolder, new DefaultItemsFilter(), itemsGetters);
    }

    public DashboardAdapter(@NonNull Context context, LoadingVH loadingHolder,
                            @NonNull DashboardItemsFilter filter, ItemsGetter... itemsGetters) {

        mContext = context;

        mLoadingHolder = loadingHolder;
        mLoadingHolder.showLoading();
        mLoadingShowed = true;

        mFilter = filter;
        mItemsGetters = itemsGetters;
    }

    public Context getContext() {
        return mContext;
    }

    public void activate() {
        if (mActivated) throw new IllegalStateException(LOG_TAG + " is still activated");
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(mItemsChangedReceiver, new IntentFilter(ACTION_ITEMS_CHANGED));
        lbm.registerReceiver(mReloadItemsReceiver, new IntentFilter(ACTION_RELOAD_ITEM));
        mActivated = true;
    }

    public void deactivate() {
        if (!mActivated) throw new IllegalStateException(LOG_TAG + " is not activated");
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReloadItemsReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mItemsChangedReceiver);
        mActivated = false;
    }

    @Override
    public void onAttachToContainer(@Nullable Object container) {
        if (!mActivated) throw new IllegalStateException("Can't attach to container, "
                + LOG_TAG + " is not activated");
        super.onAttachToContainer(container);
        notifyItemsChanged();
    }

    @Override
    public void onDetachFromContainer(@Nullable Object container) {
        super.onDetachFromContainer(container);
        saveItemsStates();
        edit().clear().setTag(EDIT_TAG).apply();
    }

    private void saveItemsStates() {
        DashboardData data = DashboardData.getter.get();
        for (ItemInfo item : getItems()) data.saveItemState(item);
    }

    @UiThread
    public void notifyReloadItem(Class<? extends ItemsGetter> itemsGetterClass) {
        if (!LoadableItemsGetter.class.isAssignableFrom(itemsGetterClass))
            throw new IllegalArgumentException("ItemsGetter must implement LoadableItemsGetter.");

        for (ItemsGetter getter : mItemsGetters)
            if (itemsGetterClass.equals(getter.getClass()))
                ((LoadableItemsGetter) getter).reload(getContext());
        notifyItemsChanged();
    }

    @UiThread
    public void notifyReloadItems() {
        for (ItemsGetter getter : mItemsGetters)
            if (getter instanceof LoadableItemsGetter)
                ((LoadableItemsGetter) getter).reload(getContext());
        notifyItemsChanged();
    }

    @UiThread
    public void notifyItemsChanged() {
        saveItemsStates();

        synchronized (mLoadingHolder) {
            boolean loading = false;
            for (ItemsGetter getter : mItemsGetters)
                loading |= getter instanceof LoadableItemsGetter &&
                        !((LoadableItemsGetter) getter).isLoaded(getContext());
            if (mLoadingShowed != loading) {
                if (mLoadingShowed) mLoadingHolder.hideLoading();
                else mLoadingHolder.showLoading();
                mLoadingShowed = !mLoadingShowed;
            }
        }

        List<ItemInfo> itemInfoList = new ArrayList<>();
        for (ItemsGetter getter : mItemsGetters)
            itemInfoList.addAll(getter.getItems(getContext()));

        mFilter.filter(itemInfoList);
        Collections.sort(itemInfoList);

        edit().clear().addAll(itemInfoList).notifyAllItemsChanged().setTag(EDIT_TAG).apply();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CustomItem.createViewHolder(getContext(), parent, viewType).forUniversalAdapter();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getItem(position).getItem(getContext()).bindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItem(getContext()).getLayoutResId(getContext());
    }

    @Override
    protected void assertAllowApplyChanges(@Nullable Object editTag, Collection<Modification<ItemInfo>> modifications,
                                           @Nullable Collection<ItemInfo> contentModifiedItems) {
        super.assertAllowApplyChanges(editTag, modifications, contentModifiedItems);
        if (EDIT_TAG != editTag) throw new UnsupportedOperationException(LOG_TAG +
                " can't be edited anytime, you can call notifyItemsChanged() or send broadcast" +
                " with action ACTION_ITEMS_CHANGED if you want to notify about items change.");
    }

    private static class DefaultItemsFilter implements DashboardItemsFilter {

        @Override
        public void filter(List<ItemInfo> items) {
            for (Iterator<ItemInfo> iterator = items.iterator(); iterator.hasNext(); )
                if (!iterator.next().isEnabled()) iterator.remove();
        }
    }
}

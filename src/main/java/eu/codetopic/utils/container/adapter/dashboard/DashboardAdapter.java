package eu.codetopic.utils.container.adapter.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.codetopic.utils.container.adapter.ArrayEditAdapter;
import eu.codetopic.utils.container.adapter.UniversalAdapter;
import eu.codetopic.utils.container.items.custom.CustomItem;

public class DashboardAdapter extends ArrayEditAdapter<ItemInfo, UniversalAdapter.ViewHolder> {

    public static final String ACTION_ITEMS_CHANGED =
            "eu.codetopic.utils.container.adapter.dashboard.DashboardAdapter.ITEMS_CHANGED";
    public static final String ACTION_RELOAD_ITEM =
            "eu.codetopic.utils.container.adapter.dashboard.DashboardAdapter.RELOAD_ITEM";
    public static final String EXTRA_CLASS_OF_ITEM_TO_RELOAD =
            "eu.codetopic.utils.container.adapter.dashboard.DashboardAdapter.CLASS_OF_ITEM_TO_RELOAD";

    private static final String LOG_TAG = "DashboardAdapter";
    private static final Object EDIT_TAG = new Object();//LOG_TAG + ".EDIT_TAG";

    private final Context mContext;
    private final DashboardItemsFilter mFilter;
    private final ItemsGetter[] mItemsGetters;

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

    public DashboardAdapter(@NonNull Context context, ItemsGetter... itemsGetters) {
        this(context, new DefaultItemsFilter(), itemsGetters);
    }

    public DashboardAdapter(@NonNull Context context, @NonNull DashboardItemsFilter filter,
                            ItemsGetter... itemsGetters) {

        mContext = context;
        mFilter = filter;
        DashboardData.initialize(context);
        mItemsGetters = itemsGetters;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onAttachToContainer(@Nullable Object container) {
        super.onAttachToContainer(container);
        getContext().registerReceiver(mItemsChangedReceiver, new IntentFilter(ACTION_ITEMS_CHANGED));
        getContext().registerReceiver(mReloadItemsReceiver, new IntentFilter(ACTION_RELOAD_ITEM));
        notifyItemsChanged();
    }

    @Override
    public void onDetachFromContainer(@Nullable Object container) {
        getContext().unregisterReceiver(mReloadItemsReceiver);
        getContext().unregisterReceiver(mItemsChangedReceiver);
        super.onDetachFromContainer(container);
        saveItemsStates();
        edit().clear().setTag(EDIT_TAG).apply();
    }

    private void saveItemsStates() {
        DashboardData data = DashboardData.getter.get();
        for (ItemInfo item : getItems()) data.saveItemState(item);
    }

    private void restoreItemsStates(Collection<ItemInfo> items) {
        DashboardData data = DashboardData.getter.get();
        for (ItemInfo item : items) data.restoreItemState(item);
    }

    @UiThread
    public void notifyReloadItem(Class<? extends ItemsGetter> itemsGetterClass) {
        if (!ReloadableItemsGetter.class.isAssignableFrom(itemsGetterClass))
            throw new IllegalArgumentException("ItemsGetter must implement ReloadableItemsGetter.");

        for (ItemsGetter getter : mItemsGetters)
            if (itemsGetterClass.equals(getter.getClass()))
                ((ReloadableItemsGetter) getter).reload(getContext());
        notifyItemsChanged();
    }

    @UiThread
    public void notifyReloadItems() {
        for (ItemsGetter getter : mItemsGetters)
            if (getter instanceof ReloadableItemsGetter)
                ((ReloadableItemsGetter) getter).reload(getContext());
        notifyItemsChanged();
    }

    @UiThread
    public void notifyItemsChanged() {
        saveItemsStates();
        List<ItemInfo> itemInfoList = new ArrayList<>();
        for (ItemsGetter getter : mItemsGetters)
            itemInfoList.addAll(getter.getItems(getContext()));

        restoreItemsStates(itemInfoList);
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

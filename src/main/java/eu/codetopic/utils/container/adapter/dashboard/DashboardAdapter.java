package eu.codetopic.utils.container.adapter.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.codetopic.utils.container.adapter.ArrayEditAdapter;
import eu.codetopic.utils.container.adapter.UniversalAdapter;
import eu.codetopic.utils.container.items.custom.CustomItem;

public class DashboardAdapter extends ArrayEditAdapter<ItemInfo, UniversalAdapter.ViewHolder> {

    public static final String ACTION_ITEMS_CHANGED =
            "eu.codetopic.utils.container.adapter.dashboard.DashboardAdapter.ITEMS_CHANGED";
    private static final String LOG_TAG = "DashboardAdapter";
    private static final Object EDIT_TAG = new Object();//LOG_TAG + ".EDIT_TAG";
    private final Context mContext;
    private final ItemsGetter[] mItemsGetters;
    private final BroadcastReceiver mItemsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyItemsChanged();
        }
    };

    public DashboardAdapter(Context context, ItemsGetter... itemsGetters) {
        mContext = context;
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
        notifyItemsChanged();
    }

    @Override
    public void onDetachFromContainer(@Nullable Object container) {
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
    public void notifyItemsChanged() {
        saveItemsStates();
        List<ItemInfo> itemInfoList = new ArrayList<>();
        for (ItemsGetter getter : mItemsGetters)
            itemInfoList.addAll(getter.getItems(getContext()));
        Collections.sort(itemInfoList);

        restoreItemsStates(itemInfoList);
        edit().clear().addAll(itemInfoList).setTag(EDIT_TAG).apply();// FIXME: 26.5.16 add support to hide not enabled items
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CustomItem.createViewHolder(getContext(), parent, viewType).forUniversalAdapter();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getItem(position).getItem().bindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItem().getLayoutResId(getContext());
    }

    @Override
    protected void assertAllowApplyChanges(@Nullable Object editTag, Collection<Modification<ItemInfo>> modifications,
                                           @Nullable Collection<ItemInfo> contentModifiedItems) {
        super.assertAllowApplyChanges(editTag, modifications, contentModifiedItems);
        if (EDIT_TAG != editTag) throw new UnsupportedOperationException(LOG_TAG +
                " can't be edited anytime, you can call notifyItemsChanged() or send broadcast" +
                " with action ACTION_ITEMS_CHANGED if you want to notify about items change.");
    }
}

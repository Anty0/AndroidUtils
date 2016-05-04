package eu.codetopic.utils.module.dashboard2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.navigation.NavigationFragment;
import eu.codetopic.utils.list.recyclerView.Recycler;
import eu.codetopic.utils.list.recyclerView.RecyclerManager;
import eu.codetopic.utils.list.recyclerView.adapter.CardRecyclerAdapter;
import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.ModulesManager;

/**
 * Created by anty on 2.5.16.
 *
 * @author anty
 */
public final class DashboardFragment extends NavigationFragment {

    public static final String ACTION_NOTIFY_UPDATE_ITEMS =
            "eu.codetopic.utils.module.dashboard2.DashboardFragment";
    private static final String LOG_TAG = "DashboardFragment";
    private static final String EXTRA_ITEMS_FILTER = "EXTRA_ITEMS_FILTER";

    private static final Comparator<DashboardItem> COMPARATOR = new Comparator<DashboardItem>() {
        @Override
        public int compare(DashboardItem lhs, DashboardItem rhs) {
            return rhs.getPriority() - lhs.getPriority();
        }
    };


    private final Object mAdapterLock = new Object();
    private CardRecyclerAdapter<DashboardItem> mAdapter = null;
    private RecyclerManager mRecycler = null;
    private DashboardItemsFilter mItemsFilter = DashboardItemsFilter.SHOW_ENABLED;
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshItems();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) mItemsFilter = (DashboardItemsFilter)
                savedInstanceState.getSerializable(EXTRA_ITEMS_FILTER);

        mAdapter = new CardRecyclerAdapter<>(getContext());

        getContext().registerReceiver(mUpdateReceiver,
                new IntentFilter(ACTION_NOTIFY_UPDATE_ITEMS));

        refreshItems();
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {

        mRecycler = Recycler.inflate().withoutSwipeToRefresh().on(inflater, container, false);
        synchronized (mAdapterLock) {
            mRecycler.setAdapter(mAdapter);
        }
        return mRecycler.getBaseView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // TODO: 25.2.16 add filter switch MenuItem
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_ITEMS_FILTER, mItemsFilter);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        mRecycler.setAdapter((RecyclerView.Adapter) null);
        mRecycler = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mUpdateReceiver);
        mAdapter = null;
        super.onDestroy();
    }

    public void setItemsFilter(@NonNull DashboardItemsFilter itemsFilter) {
        this.mItemsFilter = itemsFilter;
    }

    private List<DashboardItem> getItems() {
        ArrayList<DashboardItem> items = new ArrayList<>();
        for (Module module : ModulesManager.getInstance().getModules()) {
            DashboardItem[] toAdd = module.getDashboardItems();
            if (toAdd != null) Collections.addAll(items, toAdd);
        }

        if (mItemsFilter == null) {
            Log.e(LOG_TAG, "getItems", new NullPointerException("mItemsFilter is null," +
                    " default DashboardItemsFilter will be used"));
            mItemsFilter = DashboardItemsFilter.getDefault();
        }
        mItemsFilter.apply(items);
        Collections.sort(items, COMPARATOR);
        return items;
    }

    public void refreshItems() {
        synchronized (mAdapterLock) {
            mAdapter.edit().clear().addAll(getItems()).apply();
        }
    }


    public enum DashboardItemsFilter {
        SHOW_ALL, SHOW_ENABLED, SHOW_DISABLED;

        static DashboardItemsFilter getDefault() {
            return SHOW_ENABLED;
        }

        void apply(List<? extends DashboardItem> items) {
            switch (this) {
                case SHOW_ALL:
                    for (Iterator<? extends DashboardItem> iterator = items
                            .iterator(); iterator.hasNext(); )
                        if (!iterator.next().getData().isEnabled()) iterator.remove();
                    break;
                case SHOW_ENABLED:
                    for (Iterator<? extends DashboardItem> iterator = items
                            .iterator(); iterator.hasNext(); ) {
                        DashboardItem.ItemData item = iterator.next().getData();
                        if (!item.isEnabled() || !item.isUserEnabled()) iterator.remove();
                    }
                    break;
                case SHOW_DISABLED:
                    for (Iterator<? extends DashboardItem> iterator = items
                            .iterator(); iterator.hasNext(); ) {
                        DashboardItem.ItemData item = iterator.next().getData();
                        if (item.isUserEnabled()) iterator.remove();
                    }
                    break;
            }
        }
    }

}

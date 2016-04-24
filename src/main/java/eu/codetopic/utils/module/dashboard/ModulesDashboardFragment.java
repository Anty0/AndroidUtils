package eu.codetopic.utils.module.dashboard;

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
import eu.codetopic.utils.module.getter.DataGetter;

/**
 * Created by anty on 23.2.16.
 *
 * @author anty
 */
public class ModulesDashboardFragment extends NavigationFragment {

    public static final String ACTION_NOTIFY_UPDATE_ITEMS = "eu.codetopic.utils.module.dashboard.ModulesDashboardFragment";
    private static final String LOG_TAG = "ModulesDashboardFragment";
    private static final String EXTRA_ITEMS_FILTER = "EXTRA_ITEMS_FILTER";
    private static final String EXTRA_DATA_GETTER = "EXTRA_DATA_GETTER";

    private static final Comparator<DashboardItem> COMPARATOR = new Comparator<DashboardItem>() {
        @Override
        public int compare(DashboardItem lhs, DashboardItem rhs) {
            return rhs.getPriority() - lhs.getPriority();
        }
    };
    private final Object mAdapterLock = new Object();
    private final Object mModulesLock = new Object();
    private DashboardData mData;
    private DataGetter<? extends DashboardData> mDataGetter;
    private CardRecyclerAdapter<DashboardItem> mAdapter;
    private RecyclerManager mRecyclerManager = null;
    private DashboardItemsAdapter[] mModules;
    private DashboardItemsFilter mItemsFilter = DashboardItemsFilter.SHOW_ENABLED;
    private final BroadcastReceiver mUpdateReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onUpdate();
                }
            };

    public ModulesDashboardFragment() {
        setHasOptionsMenu(true);
    }

    public static ModulesDashboardFragment getInstance(DataGetter<? extends DashboardData> dataGetter) {
        ModulesDashboardFragment dashboardFragment = new ModulesDashboardFragment();
        dashboardFragment.mDataGetter = dataGetter;
        return dashboardFragment;
    }

    public boolean isShowDescription() {
        return mData.isShowDescription();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mItemsFilter = (DashboardItemsFilter) savedInstanceState.getSerializable(EXTRA_ITEMS_FILTER);
            //noinspection unchecked
            mDataGetter = (DataGetter<? extends DashboardData>) savedInstanceState.getSerializable(EXTRA_DATA_GETTER);
        }
        if (mDataGetter == null)
            throw new NullPointerException(LOG_TAG + " must be created using getInstance()");
        mData = mDataGetter.get();
        mAdapter = new CardRecyclerAdapter<>(getContext());
        List<DashboardItemsAdapter> dModules = new ArrayList<>();
        for (Module module : ModulesManager.getInstance().getModules()) {
            DashboardItemsAdapter[] toAdd = module.getDashboardItemsAdapters();
            if (toAdd != null) Collections.addAll(dModules, toAdd);
        }
        mModules = dModules.toArray(new DashboardItemsAdapter[dModules.size()]);

        getContext().registerReceiver(mUpdateReceiver,
                new IntentFilter(ACTION_NOTIFY_UPDATE_ITEMS));

        refreshItems();
        synchronized (mModulesLock) {
            for (DashboardItemsAdapter module : mModules) {
                module.init(mData, this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerManager = Recycler.inflate().withoutSwipeToRefresh().on(inflater, container, false);

        synchronized (mAdapterLock) {
            mRecyclerManager.setAdapter(mAdapter);
        }
        return mRecyclerManager.getBaseView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // TODO: 25.2.16 add filter switch MenuItem
    }

    public synchronized void onUpdate() {
        Log.d(LOG_TAG, "update");
        synchronized (mModulesLock) {
            for (DashboardItemsAdapter module : mModules) {
                if (module.isInitialized())
                    module.update(mData);
            }
        }
        refreshItems();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        synchronized (mModulesLock) {
            for (DashboardItemsAdapter module : mModules) {
                if (module.isInitialized())
                    module.onSaveState(mData);
            }
        }

        outState.putSerializable(EXTRA_ITEMS_FILTER, mItemsFilter);
        outState.putSerializable(EXTRA_DATA_GETTER, mDataGetter);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        mRecyclerManager.setAdapter((RecyclerView.Adapter) null);
        mRecyclerManager = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mUpdateReceiver);
        mData = null;
        mDataGetter = null;
        mAdapter = null;
        mModules = null;
        mItemsFilter = null;
        super.onDestroy();
    }

    public void setItemsFilter(@NonNull DashboardItemsFilter itemsFilter) {
        this.mItemsFilter = itemsFilter;
    }

    private List<DashboardItem> getItems() {
        ArrayList<DashboardItem> items = new ArrayList<>();
        synchronized (mModulesLock) {
            for (DashboardItemsAdapter module : mModules) {
                if (module.isInitialized())
                    Collections.addAll(items, module.getItems());
                else items.add(module.getLoadingItem());
            }
        }

        if (mItemsFilter == null) {
            Exception e = new NullPointerException("mItemsFilter is null, default DashboardItemsFilter will be used");
            Log.e(LOG_TAG, "getItems", e);
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
                        if (!iterator.next().isVisible()) iterator.remove();
                    break;
                case SHOW_ENABLED:
                    for (Iterator<? extends DashboardItem> iterator = items
                            .iterator(); iterator.hasNext(); ) {
                        DashboardItem item = iterator.next();
                        if (!item.isEnabled() || !item.isVisible()) iterator.remove();
                    }
                    break;
                case SHOW_DISABLED:
                    for (Iterator<? extends DashboardItem> iterator = items
                            .iterator(); iterator.hasNext(); ) {
                        DashboardItem item = iterator.next();
                        if (item.isEnabled()) iterator.remove();
                    }
                    break;
            }
        }
    }

}

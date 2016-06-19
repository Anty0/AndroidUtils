package eu.codetopic.utils.container.adapter.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.activity.navigation.NavigationFragment;
import eu.codetopic.utils.container.recycler.Recycler;

public abstract class DashboardFragment extends NavigationFragment {

    private final ItemsGetter[] mItemsGetters;
    private DashboardAdapter mAdapter = null;

    public DashboardFragment(ItemsGetter... itemsGetters) {
        mItemsGetters = itemsGetters;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DashboardAdapter(getContext(), getHolder(), mItemsGetters);
        mAdapter.activate();
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return Recycler.inflate().withoutSwipeToRefresh()
                .on(inflater, container, false)
                .setAdapter(mAdapter).getBaseView();
    }

    @Override
    public void onDestroy() {
        mAdapter.deactivate();
        mAdapter = null;
        super.onDestroy();
    }
}

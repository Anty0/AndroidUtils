package eu.codetopic.utils.container.adapter.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.activity.loading.LoadingFragment;
import eu.codetopic.utils.container.recycler.Recycler;
import eu.codetopic.utils.view.ViewUtils;

public abstract class DashboardFragment extends LoadingFragment {

    private final ItemsGetter[] mItemsGetters;
    private Recycler.RecyclerManagerImpl recycler = null;
    private DashboardAdapter mAdapter = null;

    public DashboardFragment(ItemsGetter... itemsGetters) {
        mItemsGetters = itemsGetters;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DashboardAdapter(getContext(), getLoadingViewHolder(), mItemsGetters);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recycler = Recycler.inflate().withoutSwipeToRefresh().on(inflater, container, false);
        recycler.setAdapter(mAdapter);
        return recycler.getBaseView();
    }

    @Override
    public void onDestroyView() {
        ViewUtils.makeViewContentStatic(getView());
        recycler.setAdapter((RecyclerView.Adapter<?>) null);
        recycler = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mAdapter = null;
        super.onDestroy();
    }
}

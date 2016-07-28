package eu.codetopic.utils.container.adapter.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.navigation.NavigationFragment;
import eu.codetopic.utils.container.recycler.Recycler;

public abstract class DashboardFragment extends NavigationFragment {

    private static final String LOG_TAG = "DashboardFragment";

    private final ItemsGetter[] mItemsGetters;
    protected DashboardAdapter mAdapter = null;
    protected Recycler.RecyclerManagerImpl mRecyclerManager;

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
        mRecyclerManager = Recycler.inflate().withoutSwipeToRefresh()
                .on(inflater, container, false)
                .setAdapter(mAdapter)
                .setItemTouchHelper(new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        ItemInfo item = mAdapter.getItem(viewHolder.getAdapterPosition());
                        return makeMovementFlags(0, item instanceof SwipeableItemInfo ? ((SwipeableItemInfo) item)
                                .getSwipeDirections(recyclerView, viewHolder) : 0);
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        ItemInfo item = mAdapter.getItem(viewHolder.getAdapterPosition());
                        if (item instanceof SwipeableItemInfo) {
                            ((SwipeableItemInfo) mAdapter.getItem(viewHolder.getAdapterPosition()))
                                    .onSwiped(viewHolder, direction);
                            return;
                        }
                        Log.e(LOG_TAG, "Detected problem in " + LOG_TAG +
                                ": received onSwiped on unsupported item -> " + item);
                    }
                });

        return mRecyclerManager.getBaseView();
    }

    @Override
    public void onDestroy() {
        mAdapter.deactivate();
        mAdapter = null;
        super.onDestroy();
    }
}

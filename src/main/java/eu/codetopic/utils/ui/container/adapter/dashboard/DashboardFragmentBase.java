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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.ui.activity.navigation.NavigationFragment;
import eu.codetopic.utils.ui.container.recycler.Recycler;

public abstract class DashboardFragmentBase extends NavigationFragment {

    private static final String LOG_TAG = "DashboardFragmentBase";

    private final ItemsGetter[] mItemsGetters;
    protected DashboardAdapter mAdapter = null;
    protected Recycler.RecyclerManagerImpl mRecyclerManager;

    public DashboardFragmentBase(ItemsGetter... itemsGetters) {
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
        mRecyclerManager = Recycler.inflate().on(inflater, container, false).setAdapter(mAdapter)
                .setItemTouchHelper(new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        int position = viewHolder.getAdapterPosition();
                        ItemInfo item = position == -1 ? null : mAdapter.getItem(position);
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
                        int position = viewHolder.getAdapterPosition();
                        ItemInfo item = position == -1 ? null : mAdapter.getItem(position);
                        if (item instanceof SwipeableItemInfo) {
                            ((SwipeableItemInfo) mAdapter.getItem(viewHolder.getAdapterPosition()))
                                    .onSwiped(viewHolder, direction);
                            //return;
                        }
                        /*Log.e(LOG_TAG, "Detected problem in " + LOG_TAG + ": " +
                                "received onSwiped on unsupported item -> " + item);*/
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

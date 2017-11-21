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

package eu.codetopic.utils.ui.container.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.Collection;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.container.adapter.CustomItemAdapter;
import eu.codetopic.utils.ui.container.adapter.UniversalAdapter;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.recycler.utils.EmptyRecyclerView;
import eu.codetopic.utils.ui.container.recycler.utils.RecyclerItemClickListener;
import eu.codetopic.utils.ui.container.swipe.SwipeLayoutManager;

public abstract class RecyclerManager<T extends RecyclerManager<T>> extends SwipeLayoutManager<T> {

    private static final String LOG_TAG = "RecyclerManager";
    private final EmptyRecyclerView mRecyclerView;
    private RecyclerView.OnItemTouchListener mLastTouchListener = null;
    private ItemTouchHelper mLastTouchHelper = null;

    protected RecyclerManager(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                              boolean useSwipeRefresh, boolean useFloatingActionButton) {
        super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        mRecyclerView = mainView.findViewById(R.id.recyclerView);
        mRecyclerView.setEmptyView(mainView.findViewById(R.id.empty_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public synchronized <DT extends CustomItem> T setAdapter(Collection<DT> adapterData) {
        return setAdapter(new CustomItemAdapter<>(getContext(), adapterData));
    }

    @SafeVarargs
    public final synchronized <DT extends CustomItem> T setAdapter(DT... adapterData) {
        return setAdapter(new CustomItemAdapter<>(getContext(), adapterData));
    }

    public synchronized T setAdapter(UniversalAdapter<?> adapter) {
        return setAdapter(adapter.forRecyclerView());
    }

    public synchronized T setAdapter(RecyclerView.Adapter<?> adapter) {
        getRecyclerView().setAdapter(adapter);
        return self();
    }

    public synchronized T setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        getRecyclerView().setLayoutManager(layoutManager);
        return self();
    }

    public synchronized T setItemTouchListener(@Nullable RecyclerItemClickListener.ClickListener
                                                       itemTouchListener) {
        RecyclerView view = getRecyclerView();
        if (mLastTouchListener != null) view.removeOnItemTouchListener(mLastTouchListener);
        mLastTouchListener = itemTouchListener == null ? null :
                new RecyclerItemClickListener(getContext(), view, itemTouchListener);
        if (mLastTouchListener != null) view.addOnItemTouchListener(mLastTouchListener);
        return self();
    }

    public synchronized T setItemTouchHelper(@Nullable ItemTouchHelper.Callback itemTouchHelperCallback) {
        if (mLastTouchHelper != null) mLastTouchHelper.attachToRecyclerView(null);
        mLastTouchHelper = itemTouchHelperCallback == null ? null
                : new ItemTouchHelper(itemTouchHelperCallback);
        if (mLastTouchHelper != null) mLastTouchHelper.attachToRecyclerView(getRecyclerView());
        return self();
    }

}

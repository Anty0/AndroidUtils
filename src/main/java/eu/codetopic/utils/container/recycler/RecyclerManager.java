package eu.codetopic.utils.container.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.Collection;

import eu.codetopic.utils.R;
import eu.codetopic.utils.container.adapter.CustomItemAdapter;
import eu.codetopic.utils.container.adapter.UniversalAdapter;
import eu.codetopic.utils.container.items.custom.CustomItem;
import eu.codetopic.utils.container.recycler.utils.EmptyRecyclerView;
import eu.codetopic.utils.container.recycler.utils.RecyclerItemClickListener;
import eu.codetopic.utils.container.swipe.SwipeLayoutManager;

public abstract class RecyclerManager<T extends RecyclerManager<T>> extends SwipeLayoutManager<T> {

    private static final String LOG_TAG = "RecyclerManager";
    private final EmptyRecyclerView mRecyclerView;
    private RecyclerView.OnItemTouchListener mLastTouchListener = null;
    private ItemTouchHelper mLastTouchHelper = null;

    protected RecyclerManager(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                              boolean useSwipeRefresh, boolean useFloatingActionButton) {
        super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        mRecyclerView = (EmptyRecyclerView) mainView.findViewById(R.id.recyclerView);
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

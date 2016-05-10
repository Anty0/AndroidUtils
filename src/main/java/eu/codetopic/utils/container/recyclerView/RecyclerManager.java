package eu.codetopic.utils.container.recyclerView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;

import eu.codetopic.utils.R;
import eu.codetopic.utils.container.items.cardview.CardItem;
import eu.codetopic.utils.container.recyclerView.adapter.CardRecyclerAdapter;
import eu.codetopic.utils.container.recyclerView.utils.EmptyRecyclerView;
import eu.codetopic.utils.container.recyclerView.utils.RecyclerItemClickListener;
import eu.codetopic.utils.container.swipe.SwipeLayoutManager;

/**
 * Created by anty on 10.4.16.
 *
 * @author anty
 */
public abstract class RecyclerManager<T extends RecyclerManager<T>> extends SwipeLayoutManager<T> {

    private static final String LOG_TAG = "RecyclerManager";
    private final EmptyRecyclerView mRecyclerView;

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

    public synchronized <DT extends CardItem> T setAdapter(Collection<DT> adapterData) {
        return setAdapter(new CardRecyclerAdapter<>(getContext(), adapterData));
    }

    @SafeVarargs
    public final synchronized <DT extends CardItem> T setAdapter(DT... adapterData) {
        return setAdapter(new CardRecyclerAdapter<>(getContext(), adapterData));
    }

    public synchronized T setAdapter(RecyclerView.Adapter adapter) {
        getRecyclerView().setAdapter(adapter);
        return self();
    }

    public synchronized T setItemTouchListener(
            RecyclerItemClickListener.ClickListener itemTouchListener) {
        RecyclerView view = getRecyclerView();
        view.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(), view, itemTouchListener));
        return self();
    }

}

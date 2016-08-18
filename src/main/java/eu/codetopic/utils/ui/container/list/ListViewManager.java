package eu.codetopic.utils.ui.container.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.container.adapter.CustomItemAdapter;
import eu.codetopic.utils.ui.container.adapter.UniversalAdapter;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.swipe.SwipeLayoutManager;

public abstract class ListViewManager<T extends ListViewManager<T>> extends SwipeLayoutManager<T> {

    private static final String LOG_TAG = "ListViewManager";
    private final ListView mListView;

    protected ListViewManager(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                              boolean useSwipeRefresh, boolean useFloatingActionButton) {
        super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        mListView = (ListView) mainView.findViewById(R.id.listView);
        mListView.setEmptyView(mainView.findViewById(R.id.empty_view));
    }

    public ListView getListView() {
        return mListView;
    }

    public synchronized <DT extends CustomItem> T setAdapter(List<DT> adapterData) {
        return setAdapter(new CustomItemAdapter<>(getContext(), adapterData));
    }

    @SafeVarargs
    public final synchronized <DT extends CustomItem> T setAdapter(DT... adapterData) {
        return setAdapter(new CustomItemAdapter<>(getContext(), adapterData));
    }

    public synchronized T setAdapter(UniversalAdapter<?> adapter) {
        return setAdapter(adapter.forListView());
    }

    public synchronized T setAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
        return self();
    }

    public synchronized T setItemClickListener(AdapterView.OnItemClickListener listener) {
        getListView().setOnItemClickListener(listener);
        return self();
    }

    public synchronized T setItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        getListView().setOnItemLongClickListener(listener);
        return self();
    }

}

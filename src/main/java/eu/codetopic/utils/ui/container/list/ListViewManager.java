/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import eu.codetopic.utils.ui.container.adapter.ExtensionsKt;
import eu.codetopic.utils.ui.container.adapter.UniversalAdapter;
import eu.codetopic.utils.ui.container.adapter.UniversalViewHolder;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.swipe.SwipeLayoutManager;

public abstract class ListViewManager<T extends ListViewManager<T>> extends SwipeLayoutManager<T> {

    private static final String LOG_TAG = "ListViewManager";
    private final ListView mListView;

    protected ListViewManager(@NonNull View mainView, @Nullable int[] swipeSchemeColors,
                              boolean useSwipeRefresh, boolean useFloatingActionButton) {
        super(mainView, swipeSchemeColors, useSwipeRefresh, useFloatingActionButton);
        mListView = mainView.findViewById(R.id.listView);
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

    public synchronized T setAdapter(UniversalAdapter<? extends UniversalViewHolder> adapter) {
        return setAdapter(ExtensionsKt.forListView(adapter));
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

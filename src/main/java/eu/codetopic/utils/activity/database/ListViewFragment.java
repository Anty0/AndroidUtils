package eu.codetopic.utils.activity.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.navigation.NavigationFragment;
import eu.codetopic.utils.database.DatabaseObjectChangeDetector;
import eu.codetopic.utils.database.DependencyTextDatabaseObject;
import eu.codetopic.utils.list.listView.MultilineAdapter;
import eu.codetopic.utils.thread.JobUtils;

public abstract class ListViewFragment<M extends DependencyTextDatabaseObject> extends NavigationFragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private final BroadcastReceiver mChangeReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    refreshData();
                }
            };
    private final Object mAdapterLock = new Object();
    private MultilineAdapter<M> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MultilineAdapter<>(getContext());
    }

    @Nullable
    @Override
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_base, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView);

        ((TextView) view.findViewById(R.id.empty_text)).setText(getEmptyViewText());
        listView.setAdapter(mAdapter);
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        view.findViewById(R.id.floatingActionButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFabClick(v);
                    }
                });
        return view;
    }

    @Override
    public void onResume() {
        ArrayList<String> actions = new ArrayList<>();
        for (Class<? extends M> clazz : getDataClasses())
            for (String action : DatabaseObjectChangeDetector
                    .getIntentFilterObjectChangedActions(clazz))
                if (!actions.contains(action)) actions.add(action);

        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) intentFilter.addAction(action);

        getContext().registerReceiver(mChangeReceiver, intentFilter);
        super.onResume();
        refreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mChangeReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mAdapter = null;
        super.onDestroy();
    }

    protected abstract CharSequence getEmptyViewText();

    public final void refreshData() {
        onDataUpdate();
    }

    protected abstract Class<? extends M>[] getDataClasses();

    protected abstract void onDataUpdate();

    protected void setData(final Collection<? extends M> data) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                synchronized (mAdapterLock) {
                    if (mAdapter == null) return;
                    mAdapter.setNotifyOnChange(false);
                    mAdapter.clear();
                    for (M obj : data)
                        mAdapter.add(obj);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public MultilineAdapter<M> getAdapter() {
        return mAdapter;
    }

    protected abstract void onFabClick(View v);

    /*@Override
    protected Class<? extends LoadingViewHolder> getViewHolderClass() {
        return ListViewViewHolder.class;
    }

    public static class ListViewViewHolder extends LoadingViewHolder {

        @Bind(R.id.loadingProgressBar) @Nullable public View loadingProgressBar;
        @Bind(R.id.floatingActionButton) @Nullable public View floatingActionButton;
        @Bind(R.id.listView) @Nullable public ListView listView;

        @Override
        protected synchronized void doShowLoading() {
            if (floatingActionButton != null)
                floatingActionButton.setVisibility(View.GONE);

            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                if (listView != null) {
                    listView.setVisibility(View.GONE);
                    View emptyView = listView.getEmptyView();
                    if (emptyView != null)
                        emptyView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        protected synchronized void doHideLoading() {
            if (floatingActionButton != null)
                floatingActionButton.setVisibility(View.VISIBLE);

            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.GONE);
                if (listView != null) {
                    listView.setVisibility(View.VISIBLE);
                    listView.setEmptyView(listView.getEmptyView());
                }
            }
        }
    }*/
}

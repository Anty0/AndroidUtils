package eu.codetopic.utils.list.recyclerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.list.items.cardview.CardItem;
import eu.codetopic.utils.list.recyclerView.adapter.CardRecyclerAdapter;
import eu.codetopic.utils.list.recyclerView.utils.EmptyRecyclerView;
import eu.codetopic.utils.list.recyclerView.utils.RecyclerItemClickListener;
import eu.codetopic.utils.module.Module;
import eu.codetopic.utils.module.ModulesManager;
import eu.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 10.4.16.
 *
 * @author anty
 */
public final class RecyclerManager {

    private static final String LOG_TAG = "RecyclerManager";

    private final Context mContext;
    private final View mMainView;
    private final EmptyRecyclerView mRecyclerView;
    private final SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerManager(@NonNull View mainView, boolean useSwipeRefresh) {
        mContext = mainView.getContext();
        mMainView = mainView;
        mRecyclerView = (EmptyRecyclerView) mainView.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainView
                .findViewById(R.id.recycler_swipe_refresh_layout);

        Log.d(LOG_TAG, "<init> for " + mContext.getClass().getName());

        mRecyclerView.setEmptyView(mainView.findViewById(R.id.empty_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));

        int[] colors = new int[0];
        for (Module module : ModulesManager.getInstance().getModules()) {
            TypedArray a = module.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, -1);
            if (color != -1) colors = Arrays.add(colors, color);
            a.recycle();
        }
        mSwipeRefreshLayout.setColorSchemeColors(colors);
        mSwipeRefreshLayout.setEnabled(useSwipeRefresh);
    }

    public Context getContext() {
        return mContext;
    }

    public synchronized View getBaseView() {
        return mMainView;
    }

    @SafeVarargs
    public final synchronized <T extends CardItem> RecyclerManager setAdapter(T... adapterData) {
        return setAdapter(new CardRecyclerAdapter<>(mContext, adapterData));
    }

    public synchronized RecyclerManager setAdapter(RecyclerView.Adapter adapter) {
        getRecyclerView().setAdapter(adapter);
        return this;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public synchronized RecyclerManager setItemTouchListener
            (RecyclerItemClickListener.ClickListener itemTouchListener) {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener
                (mContext, mRecyclerView, itemTouchListener));
        return this;
    }

    public synchronized RecyclerManager setOnRefreshListener
            (SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setOnRefreshListener(listener);
        return this;
    }

    public synchronized RecyclerManager setRefreshing(final boolean refreshing) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(refreshing);
            }
        });
        return this;
    }

    public synchronized RecyclerManager setEmptyText(CharSequence text) {
        ((TextView) mMainView.findViewById(R.id.empty_view)).setText(text);
        return this;
    }

}

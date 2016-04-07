package cz.codetopic.utils.list.recyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.codetopic.utils.Arrays;
import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;
import cz.codetopic.utils.list.items.cardview.CardItem;
import cz.codetopic.utils.list.recyclerView.adapter.CardRecyclerAdapter;
import cz.codetopic.utils.module.Module;
import cz.codetopic.utils.module.ModulesManager;
import cz.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 16.10.15.
 *
 * @author anty
 */
public final class RecyclerInflater {

    private static final String LOG_TAG = "RecyclerInflater";

    public static ActivityInflater inflate(Activity activity) {
        return new ActivityInflater(activity);
    }

    public static ViewInflater inflate(Context context, @Nullable ViewGroup parent) {
        return new ViewInflater(context, parent, true);
    }

    public static ViewInflater inflate(Context context, @Nullable ViewGroup parent,
                                       boolean attachToRoot) {
        return new ViewInflater(context, parent, attachToRoot);
    }

    private static RecyclerManager setValues(Context context, View mainView,
                                             boolean useSwipeRefresh) {
        Log.d(LOG_TAG, "setValues");
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) mainView
                .findViewById(R.id.recycler_swipe_refresh_layout);

        EmptyRecyclerView recyclerView = (EmptyRecyclerView)
                mainView.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(mainView.findViewById(R.id.empty_view));
        //recyclerView.setItemAnimator(new SpecialItemAnimator(false));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        /*recyclerView.addItemDecoration(new DividerItemDecoration(activity,
                LinearLayoutManager.VERTICAL));*/
        return new RecyclerManager(context, mainView,
                recyclerView, refreshLayout, useSwipeRefresh);
    }

    public interface Inflater {

        int DEFAULT_LAYOUT_ID = R.layout.recycler_activity;

        Inflater setLayoutResourceId(@LayoutRes @Nullable Integer layoutResourceId);

        Inflater useSwipeRefresh(boolean useSwipeRefresh);

        RecyclerManager inflate();
    }

    public static class ActivityInflater implements Inflater {

        private final Activity mActivity;
        @LayoutRes
        private Integer mLayoutResourceId = DEFAULT_LAYOUT_ID;
        private boolean mUseSwipeRefresh = true;

        private ActivityInflater(Activity activity) {
            mActivity = activity;
        }

        @Override
        public ActivityInflater setLayoutResourceId(@LayoutRes Integer layoutResourceId) {
            mLayoutResourceId = layoutResourceId;
            return this;
        }

        @Override
        public ActivityInflater useSwipeRefresh(boolean useSwipeRefresh) {
            mUseSwipeRefresh = useSwipeRefresh;
            return this;
        }

        @Override
        public RecyclerManager inflate() {
            mActivity.setContentView(mLayoutResourceId);
            return setValues(mActivity, mActivity.getWindow().getDecorView(), mUseSwipeRefresh);
        }
    }

    public static class ViewInflater implements Inflater {

        private final Context mContext;
        private final ViewGroup mParent;
        private final boolean mAttachToRoot;

        private LayoutInflater mInflater = null;
        @LayoutRes
        private Integer mLayoutResourceId = DEFAULT_LAYOUT_ID;
        private boolean mUseSwipeRefresh = true;

        private ViewInflater(Context context, @Nullable ViewGroup parent, boolean attachToRoot) {
            mContext = context;
            mParent = parent;
            mAttachToRoot = attachToRoot;
        }

        public ViewInflater setLayoutInflater(LayoutInflater inflater) {
            mInflater = inflater;
            return this;
        }

        @Override
        public ViewInflater setLayoutResourceId(@LayoutRes Integer layoutResourceId) {
            mLayoutResourceId = layoutResourceId;
            return this;
        }

        @Override
        public ViewInflater useSwipeRefresh(boolean useSwipeRefresh) {
            mUseSwipeRefresh = useSwipeRefresh;
            return this;
        }

        @Override
        public RecyclerManager inflate() {
            View result = (mInflater == null ? LayoutInflater.from(mContext) : mInflater)
                    .inflate(mLayoutResourceId, mParent, mAttachToRoot);
            return setValues(mContext, result, mUseSwipeRefresh);
        }
    }

    public static final class RecyclerManager {

        private final static String LOG_TAG = "RecyclerManager";

        private final Context mContext;
        private final View mMainView;
        private final RecyclerView mRecyclerView;
        private final SwipeRefreshLayout mSwipeRefreshLayout;

        private RecyclerManager(Context context, View mainView, RecyclerView
                recyclerView, SwipeRefreshLayout swipeRefreshLayout, boolean useSwipeRefresh) {
            Log.d(LOG_TAG, "<init>");
            mContext = context;
            mMainView = mainView;
            mRecyclerView = recyclerView;
            mSwipeRefreshLayout = swipeRefreshLayout;

            int[] colors = new int[0];
            for (Module module : ModulesManager.getInstance().getModules()) {
                TypedArray a = module.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
                int color = a.getColor(0, -1);
                if (color != -1) colors = Arrays.add(colors, color);
                a.recycle();
            }
            swipeRefreshLayout.setColorSchemeColors(colors);
            swipeRefreshLayout.setEnabled(useSwipeRefresh);
        }

        public Context getContext() {
            Log.d(LOG_TAG, "getContext");
            return mContext;
        }

        public synchronized View getBaseView() {
            Log.d(LOG_TAG, "getBaseView");
            return mMainView;
        }

        @SafeVarargs
        public final synchronized <T extends CardItem> RecyclerManager setAdapter(T... adapterData) {
            return setAdapter(new CardRecyclerAdapter<>(mContext, adapterData));
        }

        public synchronized RecyclerManager setAdapter(RecyclerView.Adapter adapter) {
            Log.d(LOG_TAG, "setAdapter");
            getRecyclerView().setAdapter(adapter);
            return this;
        }

        /*public synchronized RecyclerManager setItemAnimator(RecyclerView.ItemAnimator animator) {
            Log.d(LOG_TAG, "setItemAnimator");
            mRecyclerView.setItemAnimator(animator);
            return this;
        }*/

        public RecyclerView getRecyclerView() {
            Log.d(LOG_TAG, "getRecyclerView");
            return mRecyclerView;
        }

        public synchronized RecyclerManager setItemTouchListener
                (RecyclerItemClickListener.ClickListener itemTouchListener) {
            Log.d(LOG_TAG, "setItemTouchListener");
            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener
                    (mContext, mRecyclerView, itemTouchListener));
            return this;
        }

        public synchronized RecyclerManager setOnRefreshListener
                (SwipeRefreshLayout.OnRefreshListener listener) {
            Log.d(LOG_TAG, "setOnRefreshListener");
            mSwipeRefreshLayout.setOnRefreshListener(listener);
            return this;
        }

        public synchronized RecyclerManager setRefreshing(final boolean refreshing) {
            Log.d(LOG_TAG, "setRefreshing " + refreshing);
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
            return this;
        }

        public synchronized RecyclerManager setEmptyText(CharSequence text) {
            ((TextView) getBaseView().findViewById(R.id.empty_view)).setText(text);
            return this;
        }
    }
}

package eu.codetopic.utils.list.recyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.list.items.cardview.CardItem;
import eu.codetopic.utils.list.recyclerView.adapter.CardRecyclerAdapter;
import eu.codetopic.utils.list.recyclerView.utils.EmptyRecyclerView;
import eu.codetopic.utils.list.recyclerView.utils.RecyclerItemClickListener;
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

    RecyclerManager(@NonNull View mainView, @Nullable int[] swipeSchemeColors, boolean useSwipeRefresh) {
        mContext = mainView.getContext();
        mMainView = mainView;
        mRecyclerView = (EmptyRecyclerView) mainView.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainView
                .findViewById(R.id.recycler_swipe_refresh_layout);

        Log.d(LOG_TAG, "<init> for " + mContext.getClass().getName());

        mRecyclerView.setEmptyView(mainView.findViewById(R.id.empty_view));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mainView.getContext()));

        /*ArrayList<Module> modules = new ArrayList<>(ModulesManager.getInstance().getModules());
        Collections.sort(modules);// TODO: 9.5.16 use it to init modules colors (and use to obtaining styles attributes method in Utils)
        int[] colors = new int[0];
        for (Module module : modules) {
            TypedArray a = module.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, -1);
            if (color != -1 && !Arrays.contains(colors, color)) colors = Arrays.add(colors, color);
            a.recycle();
        }*/
        if (swipeSchemeColors != null)
            mSwipeRefreshLayout.setColorSchemeColors(swipeSchemeColors);
        mSwipeRefreshLayout.setEnabled(useSwipeRefresh);
    }

    public Context getContext() {
        return mContext;
    }

    public synchronized View getBaseView() {
        return mMainView;
    }

    public synchronized <T extends CardItem> RecyclerManager setAdapter(Collection<T> adapterData) {
        return setAdapter(new CardRecyclerAdapter<>(mContext, adapterData));
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
            (final SwipeRefreshLayout.OnRefreshListener listener) {

        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setOnRefreshListener(listener);
            }
        });
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

    public synchronized RecyclerManager setEmptyImage(final Drawable image) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) mMainView.findViewById(R.id.empty_image)).setImageDrawable(image);
            }
        });
        return this;
    }

    public synchronized RecyclerManager setEmptyImage(final Bitmap image) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) mMainView.findViewById(R.id.empty_image)).setImageBitmap(image);
            }
        });
        return this;
    }

    @TargetApi(23)
    public synchronized RecyclerManager setEmptyImage(final Icon image) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) mMainView.findViewById(R.id.empty_image)).setImageIcon(image);
            }
        });
        return this;
    }


    public synchronized RecyclerManager setEmptyImage(@DrawableRes final int imageResId) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) mMainView.findViewById(R.id.empty_image)).setImageResource(imageResId);
            }
        });
        return this;
    }

    public synchronized RecyclerManager setEmptyText(final CharSequence text) {
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) mMainView.findViewById(R.id.empty_text)).setText(text);
            }
        });
        return this;
    }

}

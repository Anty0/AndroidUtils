package eu.codetopic.utils.list.recyclerView;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import eu.codetopic.utils.R;

/**
 * Created by anty on 16.10.15.
 *
 * @author anty
 */
public final class RecyclerInflater {

    @LayoutRes public static final int DEFAULT_RECYCLER_LAYOUT_ID = R.layout.recycler_activity;
    private static final String LOG_TAG = "RecyclerInflater";
    @LayoutRes private int mLayoutResId = DEFAULT_RECYCLER_LAYOUT_ID;
    private int[] mSwipeSchemeColors = null;
    private boolean mUseSwipeToRefresh = false;

    RecyclerInflater() {
    }

    public RecyclerInflater withLayoutResId(@LayoutRes int layoutResId) {
        mLayoutResId = layoutResId;
        return this;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

    public RecyclerInflater withSwipeToRefresh() {
        mUseSwipeToRefresh = true;
        return this;
    }

    public RecyclerInflater withoutSwipeToRefresh() {
        mUseSwipeToRefresh = false;
        return this;
    }

    public boolean isUseSwipeRefresh() {
        return mUseSwipeToRefresh;
    }

    public void setUseSwipeRefresh(boolean useSwipeToRefresh) {
        this.mUseSwipeToRefresh = useSwipeToRefresh;
    }

    public RecyclerInflater withSchemeColors(int[] swipeToRefreshSchemeColors) {// TODO: 9.5.16 don't forget to use it every time
        this.mSwipeSchemeColors = swipeToRefreshSchemeColors;
        return this;
    }

    public int[] getSchemeColors() {
        return mSwipeSchemeColors;
    }

    public RecyclerManager on(Activity activity) {
        activity.setContentView(mLayoutResId);
        return new RecyclerManager(activity.getWindow().getDecorView(),
                mSwipeSchemeColors, mUseSwipeToRefresh);
    }

    public RecyclerManager on(Context context, @Nullable ViewGroup parent, boolean attachToRoot) {
        return on(LayoutInflater.from(context), parent, attachToRoot);
    }

    public RecyclerManager on(LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToRoot) {
        return new RecyclerManager(inflater.inflate(mLayoutResId, parent, attachToRoot),
                mSwipeSchemeColors, mUseSwipeToRefresh);
    }
}

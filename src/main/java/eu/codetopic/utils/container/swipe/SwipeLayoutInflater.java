package eu.codetopic.utils.container.swipe;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;

public abstract class SwipeLayoutInflater<T extends SwipeLayoutInflater<T, E>, E extends SwipeLayoutManager> {

    @LayoutRes public static final int DEFAULT_SWIPE_LAYOUT_ID = R.layout.swipe_refresh_base;
    private static final String LOG_TAG = "SwipeLayoutInflater";
    @LayoutRes private int mBaseLayoutResId = DEFAULT_SWIPE_LAYOUT_ID;
    private int[] mSwipeSchemeColors = null;
    private boolean mUseSwipeToRefresh = false;
    private boolean mUseFloatingActionButton = false;

    protected SwipeLayoutInflater() {
    }

    protected abstract T self();

    public T withBaseLayoutResId(@LayoutRes int layoutResId) {
        setBaseLayoutResId(layoutResId);
        return self();
    }

    protected int getBaseLayoutResId() {
        return mBaseLayoutResId;
    }

    protected void setBaseLayoutResId(@LayoutRes int layoutResId) {
        this.mBaseLayoutResId = layoutResId;
    }

    public T withSwipeToRefresh() {
        setUseSwipeRefresh(true);
        return self();
    }

    public T withoutSwipeToRefresh() {
        setUseSwipeRefresh(false);
        return self();
    }

    protected boolean isUseSwipeRefresh() {
        return mUseSwipeToRefresh;
    }

    protected void setUseSwipeRefresh(boolean useSwipeToRefresh) {
        this.mUseSwipeToRefresh = useSwipeToRefresh;
    }

    public T withFloatingActionButton() {
        setUseFloatingActionButton(true);
        return self();
    }

    public T withoutFloatingActionButton() {
        setUseFloatingActionButton(false);
        return self();
    }

    protected boolean isUseFloatingActionButton() {
        return mUseFloatingActionButton;
    }

    protected void setUseFloatingActionButton(boolean useFloatingActionButton) {
        this.mUseFloatingActionButton = useFloatingActionButton;
    }

    public T withSchemeColors(int[] swipeToRefreshSchemeColors) {
        setSchemeColors(swipeToRefreshSchemeColors);
        return self();
    }

    protected int[] getSchemeColors() {
        return mSwipeSchemeColors;
    }

    protected void setSchemeColors(int[] swipeSchemeColors) {
        this.mSwipeSchemeColors = swipeSchemeColors;
    }

    protected abstract E getManagerInstance(View view);

    public E on(@NonNull Activity activity) {
        return getManagerInstance(initViewFor(activity));
    }

    public E on(@NonNull Context context, @Nullable ViewGroup parent, boolean attachToRoot) {
        return on(LayoutInflater.from(context), parent, attachToRoot);
    }

    public E on(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToRoot) {
        return getManagerInstance(initViewFor(inflater, parent, attachToRoot));
    }

    protected View initViewFor(@NonNull Activity activity) {
        activity.setContentView(getBaseLayoutResId());
        return activity.getWindow().getDecorView();
    }

    protected View initViewFor(@NonNull LayoutInflater inflater,
                               @Nullable ViewGroup parent, boolean attachToRoot) {

        return inflater.inflate(getBaseLayoutResId(), parent, attachToRoot);
    }

}

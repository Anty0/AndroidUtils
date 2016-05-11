package eu.codetopic.utils.activity.loading;

import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.BackButtonActivity;

/**
 * Use {@link LoadingModule}
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class LoadingActivity extends BackButtonActivity {

    private static final String LOG_TAG = "LoadingActivity";

    private LoadingViewHolder loadingViewHolder = null;

    @Override
    public void setContentView(int layoutResID) {
        if (!hasDefaultViewHolder()) {
            super.setContentView(layoutResID);
            return;
        }

        super.setContentView(LoadingViewHolder.DEFAULT_LOADING_LAYOUT_ID);
        getLayoutInflater().inflate(layoutResID, (ViewGroup) findViewById(LoadingViewHolder.DEFAULT_CONTENT_VIEW_ID));
        updateViewHolder();
    }

    @Override
    public void setContentView(View view) {
        if (!hasDefaultViewHolder()) {
            super.setContentView(view);
            return;
        }

        super.setContentView(LoadingViewHolder.DEFAULT_LOADING_LAYOUT_ID);
        //noinspection ConstantConditions
        ((ViewGroup) findViewById(LoadingViewHolder.DEFAULT_CONTENT_VIEW_ID)).addView(view);
        updateViewHolder();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (!hasDefaultViewHolder()) {
            super.setContentView(view, params);
            return;
        }

        super.setContentView(LoadingViewHolder.DEFAULT_LOADING_LAYOUT_ID);
        //noinspection ConstantConditions
        ((ViewGroup) findViewById(LoadingViewHolder.DEFAULT_CONTENT_VIEW_ID)).addView(view, params);
        updateViewHolder();
    }

    @Override
    protected void onDestroy() {
        if (loadingViewHolder != null)
            loadingViewHolder.clearViews();
        super.onDestroy();
    }

    public boolean hasDefaultViewHolder() {
        return DefaultLoadingViewHolder.class.isAssignableFrom(getViewHolderClass());
    }

    protected Class<? extends LoadingViewHolder> getViewHolderClass() {
        return DefaultLoadingViewHolder.class;
    }

    protected void updateViewHolder() {
        if (loadingViewHolder == null) return;
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        loadingViewHolder.updateViews(root != null && root.getChildCount() > 0 ? root.getChildAt(0) : null);
    }

    public LoadingViewHolder getLoadingViewHolder() {
        if (loadingViewHolder == null) {
            try {
                loadingViewHolder = getViewHolderClass().newInstance();
                updateViewHolder();
            } catch (Exception e) {
                Log.d(LOG_TAG, "getLoadingViewHolder: provided wrong LoadingViewHolder class", e);
            }
        }
        return loadingViewHolder;
    }

}

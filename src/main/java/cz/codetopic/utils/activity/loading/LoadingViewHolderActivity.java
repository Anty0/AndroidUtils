package cz.codetopic.utils.activity.loading;

import android.view.View;
import android.view.ViewGroup;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;
import cz.codetopic.utils.activity.BackButtonActivity;

public abstract class LoadingViewHolderActivity extends BackButtonActivity {

    private static final String LOG_TAG = "LoadingViewHolderActivity";

    private LoadingViewHolder loadingViewHolder = null;

    @Override
    public void setContentView(int layoutResID) {
        if (!hasDefaultViewHolder()) {
            super.setContentView(layoutResID);
            return;
        }

        super.setContentView(R.layout.view_holder_loading_base);
        getLayoutInflater().inflate(layoutResID, (ViewGroup) findViewById(R.id.base_content));
        updateViewHolder();
    }

    @Override
    public void setContentView(View view) {
        if (!hasDefaultViewHolder()) {
            super.setContentView(view);
            return;
        }

        super.setContentView(R.layout.view_holder_loading_base);
        //noinspection ConstantConditions
        ((ViewGroup) findViewById(R.id.base_content)).addView(view);
        updateViewHolder();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (!hasDefaultViewHolder()) {
            super.setContentView(view, params);
            return;
        }

        super.setContentView(R.layout.view_holder_loading_base);
        //noinspection ConstantConditions
        ((ViewGroup) findViewById(R.id.base_content)).addView(view, params);
        updateViewHolder();
    }

    @Override
    protected void onDestroy() {
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

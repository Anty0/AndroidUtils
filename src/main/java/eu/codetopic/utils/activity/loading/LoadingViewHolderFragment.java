package eu.codetopic.utils.activity.loading;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;

public abstract class LoadingViewHolderFragment extends Fragment {

    private static final String LOG_TAG = "LoadingViewHolderFragment";

    private LoadingViewHolder loadingViewHolder = null;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        if (!hasDefaultViewHolder())
            return onCreateContentView(inflater, container, savedInstanceState);

        View base = inflater.inflate(R.layout.view_holder_loading_base, container, false);
        ViewGroup contentView = (ViewGroup) base.findViewById(R.id.base_content);
        View view = onCreateContentView(inflater, contentView, savedInstanceState);
        if (view == null) return null;
        contentView.addView(view);
        return base;
    }

    @Nullable
    public View onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateViewHolder(view);
    }

    private void updateViewHolder(View view) {
        try {
            if (loadingViewHolder != null) loadingViewHolder.updateViews(view);
        } catch (Exception e) {
            Log.e(LOG_TAG, "onViewCreated", e);
        }
    }

    @Override
    public void onDestroyView() {
        if (loadingViewHolder != null) loadingViewHolder.clearViews();
        super.onDestroyView();
    }

    public boolean hasDefaultViewHolder() {
        return DefaultLoadingViewHolder.class.isAssignableFrom(getViewHolderClass());
    }

    protected Class<? extends LoadingViewHolder> getViewHolderClass() {
        return DefaultLoadingViewHolder.class;
    }

    public LoadingViewHolder getLoadingViewHolder() {
        if (loadingViewHolder == null) {
            try {
                loadingViewHolder = getViewHolderClass().newInstance();
                updateViewHolder(getView());
            } catch (Exception e) {
                Log.e(LOG_TAG, "getLoadingViewHolder: provided wrong LoadingViewHolder class", e);
            }
        }
        return loadingViewHolder;
    }

}

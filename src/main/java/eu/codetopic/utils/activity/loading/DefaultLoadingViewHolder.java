package eu.codetopic.utils.activity.loading;

import android.support.annotation.Nullable;
import android.view.View;

public class DefaultLoadingViewHolder extends LoadingViewHolder {

    private static final String LOG_TAG = "DefaultLoadingViewHolder";

    private View loading;
    private View content;

    protected DefaultLoadingViewHolder() {
    }

    @Override
    protected void onUpdateMainView(@Nullable View newMainView) {
        if (newMainView != null) {
            loading = newMainView.findViewById(LoadingViewHolder.DEFAULT_LOADING_VIEW_ID);
            content = newMainView.findViewById(LoadingViewHolder.DEFAULT_CONTENT_VIEW_ID);
            if (loading == null || content == null)
                throw new NullPointerException("Used view is not usable for " + LOG_TAG);
        }
    }

    protected void doShowLoading() {
        if (hasAttachedView()) {
            loading.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
    }

    protected void doHideLoading() {
        if (hasAttachedView()) {
            loading.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }
}

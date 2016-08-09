package eu.codetopic.utils.view.holder.loading;

import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;

import java.lang.ref.WeakReference;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.animation.ViewVisibilityAnimator;

public abstract class LoadingVHImpl extends LoadingVH {

    private static final String LOG_TAG = "LoadingVHImpl";

    private WeakReference<View> loading = new WeakReference<>(null);
    private WeakReference<View> content = new WeakReference<>(null);

    @UiThread
    public View getContentView() {
        return content.get();
    }

    public WeakReference<View> getContentViewRef() {
        return content;
    }

    @UiThread
    public View getLoadingView() {
        return loading.get();
    }

    public WeakReference<View> getLoadingViewRef() {
        return loading;
    }

    @UiThread
    @Override
    protected void onUpdateView(@Nullable View view) {
        super.onUpdateView(view);

        LoadingWrappingInfo info = getWrappingInfo();
        View contentView = null, loadingView = null;
        if (view != null) {
            contentView = view.findViewById(info.getContentViewId());
            loadingView = view.findViewById(info.getLoadingViewId());

            if (loadingView == null || contentView == null)
                Log.e(LOG_TAG, "onUpdateView : Used view is not usable for " + LOG_TAG);
        }
        content = new WeakReference<>(contentView);
        loading = new WeakReference<>(loadingView);
    }

    @UiThread
    @Override
    protected void doShowLoading() {
        View content = getContentView(), loading = getLoadingView();
        if (content == null || loading == null) return;

        ViewVisibilityAnimator.getAnimatorFor(content).cancelAnimations();
        content.setVisibility(View.GONE);

        ViewVisibilityAnimator.getAnimatorFor(loading).fadeIn(loading.getContext()
                .getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    @UiThread
    @Override
    protected void doHideLoading() {
        View content = getContentView(), loading = getLoadingView();
        if (content == null || loading == null) return;

        ViewVisibilityAnimator.getAnimatorFor(loading).fadeOut(loading.getContext()
                .getResources().getInteger(android.R.integer.config_shortAnimTime));

        ViewVisibilityAnimator.getAnimatorFor(content).fadeIn();
    }

    @NonNull
    @Override
    protected abstract LoadingWrappingInfo getWrappingInfo();

    protected static class LoadingWrappingInfo extends WrappingInfo {

        @IdRes private final int loadingViewId;

        public LoadingWrappingInfo(@LayoutRes int wrappingLayoutRes, @IdRes int contentViewId,
                                   @IdRes int loadingViewId) {

            super(wrappingLayoutRes, contentViewId);
            this.loadingViewId = loadingViewId;
        }

        @IdRes
        @CheckResult
        public int getLoadingViewId() {
            return loadingViewId;
        }
    }
}

package eu.codetopic.utils.activity.loading;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.reflect.Method;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.thread.JobUtils;

public abstract class LoadingViewHolder {

    private static final String LOG_TAG = "LoadingViewHolder";

    private final Object lock = new Object();
    private View mainView;
    private int loadingDepth = 0;

    protected LoadingViewHolder() {
    }

    @Nullable
    public static <T extends LoadingViewHolder> T getInstance(HolderInfo<T> loadingHolderInfo) {
        try {
            return loadingHolderInfo.getHolderClass().newInstance();
        } catch (Exception e) {
            Log.d(LOG_TAG, "getLoadingViewHolder: provided wrong LoadingViewHolder class", e);
        }
        return null;
    }

    public static <T extends LoadingViewHolder> HolderInfo<T> getLoadingHolderInfo(Class<T> loadingHolderClass) {
        try {
            Method method = loadingHolderClass.getDeclaredMethod("getHolderInfo");
            method.setAccessible(true);
            //noinspection unchecked
            return (HolderInfo<T>) method.invoke(null);
        } catch (Exception e) {
            if (!(e instanceof NoSuchMethodException)) Log.d(LOG_TAG, "getLoadingHolderInfo:" +
                    " exception occurred - method \"getHolderInfo\" is not usable", e);

            RequestWrapWith annotation = loadingHolderClass.getAnnotation(RequestWrapWith.class);
            if (annotation == null) return new HolderInfo<>(loadingHolderClass);
            return new HolderInfo<>(loadingHolderClass, true,
                    annotation.wrappingLayoutRes(), annotation.contentLayoutId());
        }
    }

    final void updateViews(@Nullable View mainView) {
        synchronized (lock) {
            clearViews();
            if (mainView == null) return;
            this.mainView = mainView;
            try {
                onUpdateMainView(mainView);
                if (loadingDepth > 0) doShowLoading();
            } catch (Throwable t) {
                this.mainView = null;
                onUpdateMainView(null);
                throw t;
            }
        }
    }

    final void clearViews() {
        synchronized (lock) {
            if (loadingDepth > 0 && hasAttachedView()) doHideLoading();
            mainView = null;
            onUpdateMainView(null);
        }
    }

    protected abstract void onUpdateMainView(@Nullable View newMainView);

    public View getMainView() {
        return mainView;
    }

    public boolean hasAttachedView() {
        return mainView != null;
    }

    public final void showLoading() {
        synchronized (lock) {
            if (loadingDepth == 0 && hasAttachedView())
                JobUtils.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            doShowLoading();
                        }
                    }
                });
            loadingDepth++;
        }
    }

    protected abstract void doShowLoading();

    public final void hideLoading() {
        synchronized (lock) {
            loadingDepth--;
            if (loadingDepth == 0 && hasAttachedView())
                JobUtils.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            doHideLoading();
                        }
                    }
                });
        }
    }

    protected abstract void doHideLoading();

    public final boolean isLoadingShowed() {
        synchronized (lock) {
            return loadingDepth > 0;
        }
    }

    public static final class HolderInfo<T extends LoadingViewHolder> {

        public static final int NO_RES_OR_ID = -1;

        private final Class<T> holderClass;
        private final boolean requestsWrap;
        @LayoutRes private final int wrappingLayoutRes;
        @IdRes private final int contentLayoutId;

        public HolderInfo(Class<T> holderClass) {
            this(holderClass, false, NO_RES_OR_ID, NO_RES_OR_ID);
        }

        public HolderInfo(Class<T> holderClass, boolean requestsWrap, @LayoutRes int wrappingLayoutRes,
                          @IdRes int contentLayoutId) {

            this.holderClass = holderClass;
            this.requestsWrap = requestsWrap;
            this.wrappingLayoutRes = wrappingLayoutRes;
            this.contentLayoutId = contentLayoutId;
        }

        public Class<T> getHolderClass() {
            return holderClass;
        }

        public boolean isRequestsWrap() {
            return requestsWrap;
        }

        @LayoutRes
        public int getWrappingLayoutRes() {
            return wrappingLayoutRes;
        }

        @IdRes
        public int getContentLayoutId() {
            return contentLayoutId;
        }
    }
}

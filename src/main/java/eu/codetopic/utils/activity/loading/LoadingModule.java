package eu.codetopic.utils.activity.loading;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.modular.ModularActivity;
import eu.codetopic.utils.activity.modular.SimpleActivityCallBackModule;

public class LoadingModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "LoadingModule";

    private LoadingViewHolder loadingViewHolder = null;

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        if (!hasDefaultViewHolder()) {
            callBack.pass();
            return;
        }

        callBack.set(LoadingViewHolder.DEFAULT_LOADING_LAYOUT_ID);
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                ModularActivity activity = getActivity();
                activity.getLayoutInflater().inflate(layoutResID,
                        (ViewGroup) activity.findViewById(LoadingViewHolder
                                .DEFAULT_CONTENT_VIEW_ID));
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        if (!hasDefaultViewHolder()) {
            callBack.pass();
            return;
        }

        callBack.set(LoadingViewHolder.DEFAULT_LOADING_LAYOUT_ID);
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(LoadingViewHolder
                        .DEFAULT_CONTENT_VIEW_ID)).addView(view);
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params,
                                    SetContentViewCallBack callBack) {

        if (!hasDefaultViewHolder()) {
            callBack.pass();
            return;
        }

        callBack.set(LoadingViewHolder.DEFAULT_LOADING_LAYOUT_ID);
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(LoadingViewHolder
                        .DEFAULT_CONTENT_VIEW_ID)).addView(view, params);
                updateViewHolder();
            }
        });
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
        ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
        loadingViewHolder.updateViews(root != null && root
                .getChildCount() > 0 ? root.getChildAt(0) : null);
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

package eu.codetopic.utils.activity.loading;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.activity.modular.ModularActivity;
import eu.codetopic.utils.activity.modular.SimpleActivityCallBackModule;

public class LoadingModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "LoadingModule";

    private LoadingViewHolder loadingViewHolder = null;
    private LoadingViewHolder.HolderInfo<?> loadingHolderInfo = null;

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        final LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap()) {
            callBack.pass();
            return;
        }

        callBack.set(holderInfo.getWrappingLayoutRes());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                ModularActivity activity = getActivity();
                activity.getLayoutInflater().inflate(layoutResID, (ViewGroup) activity
                        .findViewById(holderInfo.getContentLayoutId()));
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        final LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap()) {
            callBack.pass();
            return;
        }

        callBack.set(holderInfo.getWrappingLayoutRes());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(holderInfo
                        .getContentLayoutId())).addView(view);
                updateViewHolder();
            }
        });
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params,
                                    SetContentViewCallBack callBack) {

        final LoadingViewHolder.HolderInfo<?> holderInfo = getLoadingHolderInfo();
        if (!holderInfo.isRequestsWrap()) {
            callBack.pass();
            return;
        }

        callBack.set(holderInfo.getWrappingLayoutRes());
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(holderInfo
                        .getContentLayoutId())).addView(view, params);
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

    protected void updateViewHolder() {
        if (loadingViewHolder == null) return;
        ViewGroup root = (ViewGroup) getActivity().findViewById(android.R.id.content);
        loadingViewHolder.updateViews(root != null && root
                .getChildCount() > 0 ? root.getChildAt(0) : null);
    }

    public LoadingViewHolder getLoadingViewHolder() {
        if (loadingViewHolder == null) {
            loadingViewHolder = LoadingViewHolder
                    .getInstance(getLoadingHolderInfo());
            updateViewHolder();
        }
        return loadingViewHolder;
    }

    public LoadingViewHolder.HolderInfo<?> getLoadingHolderInfo() {
        if (loadingHolderInfo == null) {
            loadingHolderInfo = LoadingViewHolder
                    .getLoadingHolderInfo(getViewHolderClass());
        }
        return loadingHolderInfo;
    }

    protected Class<? extends LoadingViewHolder> getViewHolderClass() {
        return DefaultLoadingViewHolder.class;
    }

}

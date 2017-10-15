package eu.codetopic.utils.ui.activity.modular.module;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

public class ToolbarModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "ToolbarModule";

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(() -> {
            ModularActivity activity = getActivity();
            activity.getLayoutInflater().inflate(layoutResID, activity.findViewById(R.id.base_content));
        });
        setupCallback(callBack);
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_content)).addView(view);
        });
        setupCallback(callBack);
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_content)).addView(view, params);
        });
        setupCallback(callBack);
    }

    private void setupCallback(SetContentViewCallBack callBack) {
        callBack.addViewAttachedCallBack(() -> {
            ModularActivity activity = getActivity();
            activity.setSupportActionBar(activity.findViewById(R.id.toolbar));
        });
    }
}

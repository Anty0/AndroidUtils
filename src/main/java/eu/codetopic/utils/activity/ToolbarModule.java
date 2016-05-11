package eu.codetopic.utils.activity;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.activity.modular.ModularActivity;
import eu.codetopic.utils.activity.modular.SimpleActivityCallBackModule;

/**
 * Created by anty on 11.5.16.
 *
 * @author anty
 */
public class ToolbarModule extends SimpleActivityCallBackModule {

    private static final String LOG_TAG = "ToolbarModule";

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                ModularActivity activity = getActivity();
                activity.getLayoutInflater().inflate(layoutResID,
                        (ViewGroup) activity.findViewById(R.id.base_content));
            }
        });
        setupCallback(callBack);
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(R.id.base_content)).addView(view);
            }
        });
        setupCallback(callBack);
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params, SetContentViewCallBack callBack) {
        callBack.set(R.layout.toolbar_base);
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                //noinspection ConstantConditions
                ((ViewGroup) getActivity().findViewById(R.id.base_content)).addView(view, params);
            }
        });
        setupCallback(callBack);
    }

    private void setupCallback(SetContentViewCallBack callBack) {
        callBack.addViewAttachedCallBack(new Runnable() {
            @Override
            public void run() {
                ModularActivity activity = getActivity();
                activity.setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
            }
        });
    }
}

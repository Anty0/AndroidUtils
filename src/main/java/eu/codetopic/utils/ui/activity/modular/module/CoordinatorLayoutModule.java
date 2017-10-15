package eu.codetopic.utils.ui.activity.modular.module;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.R;
import eu.codetopic.utils.ui.activity.modular.ModularActivity;
import eu.codetopic.utils.ui.activity.modular.SimpleActivityCallBackModule;

/**
 * Created by anty on 10/13/17.
 *
 * @author anty
 */
public class CoordinatorLayoutModule extends SimpleActivityCallBackModule {

    @Override
    protected void onSetContentView(@LayoutRes final int layoutResID, SetContentViewCallBack callBack) {
        callBack.set(R.layout.cordinator_layout_base);
        callBack.addViewAttachedCallBack(() -> {
            ModularActivity activity = getActivity();
            activity.getLayoutInflater().inflate(layoutResID, activity.findViewById(R.id.base_coordinator_layout_content));
        });
    }

    @Override
    protected void onSetContentView(final View view, SetContentViewCallBack callBack) {
        callBack.set(R.layout.cordinator_layout_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_coordinator_layout_content)).addView(view);
        });
    }

    @Override
    protected void onSetContentView(final View view, final ViewGroup.LayoutParams params, SetContentViewCallBack callBack) {
        callBack.set(R.layout.cordinator_layout_base);
        callBack.addViewAttachedCallBack(() -> {
            ((ViewGroup) getActivity().findViewById(R.id.base_coordinator_layout_content)).addView(view, params);
        });
    }
}

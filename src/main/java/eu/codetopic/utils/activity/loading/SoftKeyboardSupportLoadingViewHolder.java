package eu.codetopic.utils.activity.loading;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import eu.codetopic.utils.R;
import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Created by anty on 24.5.16.
 *
 * @author anty
 */
public class SoftKeyboardSupportLoadingViewHolder extends LoadingViewHolderImpl {

    @LayoutRes private static final int LOADING_LAYOUT_ID = R.layout.loading_soft_keyboard_support_base;
    @IdRes private static final int CONTENT_VIEW_ID = R.id.base_loadable_content;
    @IdRes private static final int LOADING_VIEW_ID = R.id.base_loading;

    private static final String LOG_TAG = "SoftKeyboardSupportLoadingViewHolder";

    @Keep
    @KeepName
    private static HolderInfo<DefaultLoadingViewHolder> getHolderInfo() {
        return new HolderInfo<>(DefaultLoadingViewHolder.class, true,
                LOADING_LAYOUT_ID, CONTENT_VIEW_ID);
    }

    @Override
    protected int getContentViewId(Context context) {
        return CONTENT_VIEW_ID;
    }

    @Override
    protected int getLoadingViewId(Context context) {
        return LOADING_VIEW_ID;
    }
}

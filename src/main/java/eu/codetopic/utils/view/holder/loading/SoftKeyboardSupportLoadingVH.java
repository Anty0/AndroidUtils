package eu.codetopic.utils.view.holder.loading;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import eu.codetopic.utils.R;

public class SoftKeyboardSupportLoadingVH extends DefaultLoadingVH {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.loading_soft_keyboard_support_base;

    private static final String LOG_TAG = "SoftKeyboardSupportLoadingVH";

    @NonNull
    @Override
    protected LoadingWrappingInfo getWrappingInfo() {
        return new LoadingWrappingInfo(LOADING_LAYOUT_ID, CONTENT_VIEW_ID, LOADING_VIEW_ID);
    }
}

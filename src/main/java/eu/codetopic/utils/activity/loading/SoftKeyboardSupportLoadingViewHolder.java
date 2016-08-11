package eu.codetopic.utils.activity.loading;

import android.support.annotation.LayoutRes;

import eu.codetopic.utils.R;
import proguard.annotation.Keep;
import proguard.annotation.KeepName;

/**
 * Use eu.codetopic.utils.view.holder.loading.SoftKeyboardSupportLoadingVH instead
 */
@Deprecated
@SuppressWarnings("deprecation")
public class SoftKeyboardSupportLoadingViewHolder extends DefaultLoadingViewHolder {

    @LayoutRes protected static final int LOADING_LAYOUT_ID = R.layout.loading_soft_keyboard_support_base;

    private static final String LOG_TAG = "SoftKeyboardSupportLoadingViewHolder";

    @Keep
    @KeepName
    private static HolderInfo<DefaultLoadingViewHolder> getHolderInfo() {
        return new HolderInfo<>(DefaultLoadingViewHolder.class, true,
                LOADING_LAYOUT_ID, CONTENT_VIEW_ID);
    }
}

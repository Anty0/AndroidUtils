package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anty on 15.5.16.
 *
 * @author anty
 */
public interface CustomItem {

    int NO_POSITION = -1;
    int NO_LAYOUT_RES_ID = -1;

    @Nullable
    View getView(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition);

    @LayoutRes
    int getLayoutResId(Context context, int itemPosition);
}

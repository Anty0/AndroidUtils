package eu.codetopic.utils.list.items.cardview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anty on 21.2.16.
 *
 * @author anty
 */
public interface CardItem {

    int NO_LAYOUT_RES_ID = -1;

    View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition);

    int getLayoutResId(Context context, int itemPosition);

    void onClick(Context context, View v, int itemPosition);

    boolean onLongClick(Context context, View v, int itemPosition);
}

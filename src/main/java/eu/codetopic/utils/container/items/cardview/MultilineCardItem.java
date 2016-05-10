package eu.codetopic.utils.container.items.cardview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.MultilineItemUtils;

/**
 * Created by anty on 22.2.16.
 *
 * @author anty
 */
public abstract class MultilineCardItem extends CardItemNoClickImpl implements MultilineItem {

    @Override
    public View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        return MultilineItemUtils.apply(this).withoutPadding()
                .withDefaultLayoutResId(getLayoutResId(context, itemPosition))
                .withPosition(itemPosition).on(context, parent, oldView);
    }

    @Override
    @LayoutRes
    public final int getLayoutResId(Context context, int itemPosition) {
        return MultilineItemUtils.getLayoutResIdFor(context, this, itemPosition, null);
    }
}

package cz.codetopic.utils.list.items.cardview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import cz.codetopic.utils.list.items.multiline.MultilineItem;
import cz.codetopic.utils.list.items.multiline.MultilineItemUtils;
import cz.codetopic.utils.list.items.multiline.MultilineResourceLayoutItem;

/**
 * Created by anty on 22.2.16.
 *
 * @author anty
 */
public abstract class MultilineCardItem extends CardItemNoClickImpl implements MultilineItem {

    @Override
    public View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        if (oldView != null) {
            MultilineItemUtils.applyMultilineItemOnView(oldView, this);
            return oldView;
        }
        return MultilineItemUtils.applyMultilineItemOnView(context,
                parent, this, getLayoutResId(context, itemPosition));
    }

    @Override
    @LayoutRes
    public final int getLayoutResId(Context context, int itemPosition) {
        return this instanceof MultilineResourceLayoutItem ? ((MultilineResourceLayoutItem) this)
                .getLayoutResourceId(context, itemPosition) : DEFAULT_ITEM_LAYOUT_ID;
    }
}

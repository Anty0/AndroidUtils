package eu.codetopic.utils.container.items.cardview;

import android.content.Context;
import android.view.View;

import eu.codetopic.utils.container.items.custom.ClickableCustomItem;

public abstract class CardItemNoClickImpl extends CardItem implements ClickableCustomItem {

    @Override
    public void onClick(Context context, View v, int itemPosition) {

    }

    @Override
    public boolean onLongClick(Context context, View v, int itemPosition) {
        return false;
    }
}

package eu.codetopic.utils.list.items.cardview;

import android.content.Context;
import android.view.View;

/**
 * Created by anty on 15.3.16.
 *
 * @author anty
 */
public abstract class CardItemNoClickImpl implements CardItem {

    @Override
    public void onClick(Context context, View v, int itemPosition) {

    }

    @Override
    public boolean onLongClick(Context context, View v, int itemPosition) {
        return false;
    }
}

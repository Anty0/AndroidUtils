package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.view.View;

/**
 * Created by anty on 16.5.16.
 *
 * @author anty
 */
public interface ClickableCustomItem extends CustomItem {

    void onClick(Context context, View v, int itemPosition);

    boolean onLongClick(Context context, View v, int itemPosition);
}

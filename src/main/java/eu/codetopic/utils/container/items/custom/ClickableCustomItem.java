package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.view.View;

public interface ClickableCustomItem extends CustomItem {

    void onClick(Context context, View v, int itemPosition);

    boolean onLongClick(Context context, View v, int itemPosition);
}

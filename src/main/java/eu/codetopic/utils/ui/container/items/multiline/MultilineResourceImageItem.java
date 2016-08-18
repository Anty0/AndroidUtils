package eu.codetopic.utils.ui.container.items.multiline;

import android.content.Context;
import android.support.annotation.DrawableRes;

public interface MultilineResourceImageItem extends MultilineItem {

    @DrawableRes
    int getImageResourceId(Context context, int position);
}

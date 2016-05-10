package eu.codetopic.utils.container.items.multiline;

import android.content.Context;
import android.support.annotation.DrawableRes;

/**
 * Created by anty on 30.9.15.
 *
 * @author anty
 */
public interface MultilineResourceImageItem extends MultilineItem {

    @DrawableRes
    int getImageResourceId(Context context, int position);
}

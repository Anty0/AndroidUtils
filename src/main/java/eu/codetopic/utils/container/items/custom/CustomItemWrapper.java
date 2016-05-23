package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;

/**
 * Created by anty on 23.5.16.
 *
 * @author anty
 */
public abstract class CustomItemWrapper extends CustomItem {

    @IdRes
    protected abstract int getContentViewId(Context context);

}

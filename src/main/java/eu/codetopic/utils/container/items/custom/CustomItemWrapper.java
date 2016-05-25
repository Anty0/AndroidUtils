package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;

public abstract class CustomItemWrapper extends CustomItem {

    @IdRes
    protected abstract int getContentViewId(Context context);

}

package eu.codetopic.utils.container.items.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public abstract class CustomItemWrapper extends CustomItem {

    public CustomItemWrapper(@NonNull CustomItemWrapper... wrappers) {
        super(wrappers);
    }

    @IdRes
    protected abstract int getContentViewId(Context context);

}

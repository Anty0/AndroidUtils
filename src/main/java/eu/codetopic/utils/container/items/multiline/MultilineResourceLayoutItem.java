package eu.codetopic.utils.container.items.multiline;

import android.content.Context;
import android.support.annotation.LayoutRes;

public interface MultilineResourceLayoutItem extends MultilineItem {

    @LayoutRes
    int getLayoutResourceId(Context context);
}

package cz.codetopic.utils.list.items.multiline;

import android.content.Context;
import android.support.annotation.LayoutRes;

/**
 * Created by anty on 30.9.15.
 *
 * @author anty
 */
public interface MultilineResourceLayoutItem extends MultilineItem {

    @LayoutRes
    int getLayoutResourceId(Context context, int position);
}

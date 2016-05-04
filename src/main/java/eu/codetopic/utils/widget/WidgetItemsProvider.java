package eu.codetopic.utils.widget;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import eu.codetopic.utils.list.items.multiline.MultilineItem;

/**
 * Created by anty on 22.3.16.
 *
 * @author anty
 */
public interface WidgetItemsProvider extends Serializable {

    @NonNull
    List<? extends MultilineItem> getItems() throws Throwable;
}

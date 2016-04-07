package cz.codetopic.utils.list.widget;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import cz.codetopic.utils.list.items.multiline.MultilineItem;

/**
 * Created by anty on 22.3.16.
 *
 * @author anty
 */
public interface WidgetItemsProvider extends Serializable {

    @NonNull
    List<MultilineItem> getItems();
}

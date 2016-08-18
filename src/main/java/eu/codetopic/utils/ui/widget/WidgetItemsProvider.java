package eu.codetopic.utils.ui.widget;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import eu.codetopic.utils.ui.container.items.multiline.MultilineItem;

public interface WidgetItemsProvider extends Serializable {

    @NonNull
    List<? extends MultilineItem> getItems() throws Throwable;
}

package com.codetopic.utils.widget;

import android.support.annotation.NonNull;

import com.codetopic.utils.container.items.multiline.MultilineItem;

import java.io.Serializable;
import java.util.List;

public interface WidgetItemsProvider extends Serializable {

    @NonNull
    List<? extends MultilineItem> getItems() throws Throwable;
}

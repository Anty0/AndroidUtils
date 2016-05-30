package eu.codetopic.utils.container.items.custom;

import android.support.annotation.NonNull;

import eu.codetopic.utils.container.items.multiline.MultilineItem;

public abstract class MultilineCustomItem extends MultilineItemCustomItemWrapper
        implements MultilineItem {

    public MultilineCustomItem(@NonNull CustomItemWrapper... wrappers) {
        super(null, wrappers);
    }
}

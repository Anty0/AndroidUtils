package eu.codetopic.utils.container.adapter.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Collection;

public interface ItemsGetter {

    @NonNull
    Collection<? extends ItemInfo> getItems(Context context);
}

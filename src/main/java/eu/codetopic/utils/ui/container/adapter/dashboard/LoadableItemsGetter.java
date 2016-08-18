package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.Context;
import android.support.annotation.UiThread;

public interface LoadableItemsGetter extends ItemsGetter {

    @UiThread
    void reload(Context context);

    @UiThread
    boolean isLoaded(Context context);
}

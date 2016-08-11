package com.codetopic.utils.container.adapter.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Collection;

public interface ItemsGetter {

    @NonNull
    @UiThread
    Collection<? extends ItemInfo> getItems(Context context);
}

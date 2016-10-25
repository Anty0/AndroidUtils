package eu.codetopic.utils.ui.container.adapter;

import android.app.PendingIntent;
import android.content.Context;

public interface WidgetItemClickListener {

    PendingIntent getOnClickIntent(Context context);
}

package eu.codetopic.utils.ui.container.adapter.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViewsService;

import eu.codetopic.java.utils.log.Log;

@TargetApi(11)
public class CustomItemWidgetAdapterService extends RemoteViewsService {

    private static final String LOG_TAG = "CustomItemWidgetAdapterService";

    private static final String EXTRA_ITEMS_PROVIDER = "eu.codetopic.utils.ui.container.adapter.widget."
            + LOG_TAG + ".EXTRA_ITEMS_PROVIDER";

    public static Intent getIntent(Context context, WidgetCustomItemsProvider itemsProvider) {
        Intent intent = new Intent(context, CustomItemWidgetAdapterService.class)
                .putExtra(EXTRA_ITEMS_PROVIDER, itemsProvider);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetCustomItemsProvider itemsProvider = (WidgetCustomItemsProvider) intent.getSerializableExtra(EXTRA_ITEMS_PROVIDER);
        if (itemsProvider == null) {
            Log.e(LOG_TAG, "onGetViewFactory -> EXTRA_ITEMS_PROVIDER not found in intent, list will be empty");
            itemsProvider = new SimpleWidgetCustomItemsProvider();
        }
        return new CustomItemWidgetAdapter(this, itemsProvider).forWidget(this, null);
    }
}

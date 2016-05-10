package eu.codetopic.utils.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.container.items.multiline.MultilineItem;

/**
 * Created by anty on 22.7.15.
 *
 * @author anty
 */
@TargetApi(11)
public class WidgetMultilineAdapter implements RemoteViewsService.RemoteViewsFactory {

    static final String EXTRA_ITEMS_PROVIDER =
            "eu.codetopic.utils.widget.WidgetService.EXTRA_ITEMS_PROVIDER";
    private static final String LOG_TAG = "WidgetMultilineAdapter";
    private final Context mContext;
    private final WidgetItemsProvider mItemsProvider;
    private final ArrayList<MultilineItem> mItems = new ArrayList<>();

    public WidgetMultilineAdapter(Context context, Intent intent) {
        Log.d(LOG_TAG, "<init>");
        mContext = context;
        WidgetItemsProvider provider = (WidgetItemsProvider) intent
                .getSerializableExtra(EXTRA_ITEMS_PROVIDER);
        mItemsProvider = provider != null ? provider : new WidgetItemsProvider() {
            @NonNull
            @Override
            public List<MultilineItem> getItems() {
                return new ArrayList<>();
            }
        };
    }

    public static Intent getServiceIntent(Context context, WidgetItemsProvider itemsProvider) {
        Intent intent = new Intent(context, WidgetService.class)
                .putExtra(EXTRA_ITEMS_PROVIDER, itemsProvider);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));// TODO: 5.5.16 use or not use?
        // FIXME: 5.5.16 don't working (i don't know why)
        return intent;
    }

    private void updateItems() {
        Log.d(LOG_TAG, "updateItems");
        mItems.clear();
        try {
            mItems.addAll(mItemsProvider.getItems());
        } catch (Throwable t) {
            Log.e(LOG_TAG, "updateItems", t);
        }
    }

    @Override
    public void onCreate() {
        updateItems();
    }

    @Override
    public void onDataSetChanged() {
        updateItems();
    }

    @Override
    public void onDestroy() {
        mItems.clear();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.widget_list_item_multi_line_text);
        MultilineItem multilineItem = mItems.get(position);
        remoteView.setTextViewText(R.id.widget_text_view_title, multilineItem.getTitle(mContext, position));
        remoteView.setTextViewText(R.id.widget_text_view_text, multilineItem.getText(mContext, position));
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}

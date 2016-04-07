package cz.codetopic.utils.list.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;
import cz.codetopic.utils.list.items.multiline.MultilineItem;

/**
 * Created by anty on 22.7.15.
 *
 * @author anty
 */
@TargetApi(11)
public class WidgetMultilineAdapter implements RemoteViewsService.RemoteViewsFactory {

    static final String EXTRA_ITEMS_PROVIDER = "cz.codetopic.utils.list.widget.WidgetService.EXTRA_ITEMS_PROVIDER";
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

    private void updateItems() {
        Log.d(LOG_TAG, "updateItems");
        mItems.clear();
        mItems.addAll(mItemsProvider.getItems());
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        updateItems();
    }

    @Override
    public void onDataSetChanged() {
        Log.d(LOG_TAG, "onDataSetChanged");
        updateItems();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        mItems.clear();
    }

    @Override
    public int getCount() {
        Log.d(LOG_TAG, "getCount: " + mItems.size());
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        Log.d(LOG_TAG, "getItemId: " + position);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        Log.d(LOG_TAG, "hasStableIds: " + false);
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(LOG_TAG, "getViewAt: " + position);
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.widget_list_item_multi_line_text);
        MultilineItem multilineItem = mItems.get(position);
        remoteView.setTextViewText(R.id.widget_text_view_title, multilineItem.getTitle(mContext, position));
        remoteView.setTextViewText(R.id.widget_text_view_text, multilineItem.getText(mContext, position));
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.d(LOG_TAG, "getLoadingView: " + null);
        return null;
    }

    @Override
    public int getViewTypeCount() {
        Log.d(LOG_TAG, "getViewTypeCount: " + 1);
        return 1;
    }
}

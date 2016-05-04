package eu.codetopic.utils.module.dashboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import eu.codetopic.utils.Utils;
import eu.codetopic.utils.list.items.multiline.MultilineItem;
import eu.codetopic.utils.list.items.multiline.MultilineItemUtils;

/**
 * Created by anty on 23.2.16.
 *
 * @author anty
 */
@Deprecated
@SuppressWarnings("deprecation")
public abstract class MultilineDashboardItem extends DashboardItem implements MultilineItem {

    private static final String TAG_CONTENT_VIEW = "CONTENT_VIEW";

    public MultilineDashboardItem(DashboardItemsAdapter module) {
        super(module);
    }

    @Override
    public View onCreateView(Context context, ViewGroup parent, int position) {
        LinearLayout body = new LinearLayout(context);

        MultilineItemUtils.apply(null)
                .withDefaultLayoutResId(getMultilineLayoutRes(context, position))
                .withPosition(position)
                .on(context, body, true);

        ViewGroup content = new FrameLayout(context);
        content.setTag(TAG_CONTENT_VIEW);
        Utils.setPadding(content, 15, 3, 1, 0);
        body.addView(content);
        return body;
    }

    @Override
    public void onUpdateView(Context context, View view, int position) {
        MultilineItemUtils.apply(this)
                .withDefaultLayoutResId(getMultilineLayoutRes(context, position))
                .withPosition(position)
                .on(view);

        ViewGroup content = (ViewGroup) view.findViewWithTag(TAG_CONTENT_VIEW);
        content.removeAllViews();
        View contentView = getContentView(content, position);
        if (contentView != null) content.addView(contentView);
        content.setVisibility(contentView == null ? View.GONE : View.VISIBLE);
    }

    @LayoutRes
    protected int getMultilineLayoutRes(Context context, int position) {
        return DEFAULT_ITEM_LAYOUT_ID;
    }

    @Nullable
    protected View getContentView(ViewGroup parent, int position) {
        return null;
    }
}

package eu.codetopic.utils.module.dashboard2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;
import eu.codetopic.utils.list.items.multiline.MultilineItem;
import eu.codetopic.utils.list.items.multiline.MultilineItemUtils;
import eu.codetopic.utils.list.items.multiline.MultilineResourceLayoutItem;

/**
 * Created by anty on 2.5.16.
 *
 * @author anty
 */
public abstract class MultilineDashboardItem extends DashboardItem implements MultilineItem {

    private static final String LOG_TAG = "MultilineDashboardItem";
    private static final String TAG_CONTENT_VIEW = "CONTENT_VIEW";
    private static final String TAG_BASE_FRAME = "BASE_FRAME";
    private static final String TAG_LOADING_FRAME = "LOADING_BASE_FRAME";

    public MultilineDashboardItem(ItemData data) {
        super(data);
    }

    private static View generateLoadingView(Context context, ViewGroup parent,
                                            int position, CharSequence title) {

        return MultilineItemUtils.apply(new LoadingMultilineItem(title))
                .withPosition(position)
                .on(context, parent, false);
    }

    @Override
    public View onCreateView(Context context, ViewGroup parent, int position) {
        LinearLayout body = new LinearLayout(context);

        ViewGroup base = new FrameLayout(context);
        base.setTag(TAG_BASE_FRAME);
        body.addView(base);

        MultilineItemUtils.apply(null)
                .withPosition(position)
                .on(context, base, true);

        ViewGroup content = new FrameLayout(context);
        content.setTag(TAG_CONTENT_VIEW);
        Utils.setPadding(content, 15, 3, 1, 0);
        base.addView(content);

        ViewGroup loadingBase = new FrameLayout(context);
        loadingBase.setTag(TAG_LOADING_FRAME);
        loadingBase.setVisibility(View.GONE);
        body.addView(loadingBase);
        loadingBase.addView(generateLoadingView(context, loadingBase,
                position, getTitle(context, position)));
        return body;
    }

    @Override
    public void onUpdateView(Context context, View body, int position) {
        MultilineItemUtils.apply(this)
                .withPosition(position)
                .on(((ViewGroup) body.findViewWithTag(TAG_BASE_FRAME)).getChildAt(0));

        ViewGroup content = (ViewGroup) body.findViewWithTag(TAG_CONTENT_VIEW);
        content.removeAllViews();
        View contentView = getContentView(content, position);
        if (contentView != null) content.addView(contentView);
        content.setVisibility(contentView == null ? View.GONE : View.VISIBLE);

        boolean loading = isShowAsLoading();
        body.findViewWithTag(TAG_BASE_FRAME).setVisibility(loading ? View.GONE : View.VISIBLE);
        body.findViewWithTag(TAG_LOADING_FRAME).setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Nullable
    protected View getContentView(ViewGroup parent, int position) {
        return null;
    }

    protected boolean isShowAsLoading() {
        return false;
    }

    private static class LoadingMultilineItem implements MultilineResourceLayoutItem {

        private final CharSequence title;

        public LoadingMultilineItem(CharSequence title) {
            this.title = title;
        }

        @Nullable
        @Override
        public CharSequence getTitle(Context context, int position) {
            return title;
        }

        @Nullable
        @Override
        public CharSequence getText(Context context, int position) {
            return context.getText(R.string.wait_text_loading);
        }

        @Override
        public int getLayoutResourceId(Context context, int position) {
            return R.layout.listitem_multiline_loading;
        }
    }
}

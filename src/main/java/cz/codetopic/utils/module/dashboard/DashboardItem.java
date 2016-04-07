package cz.codetopic.utils.module.dashboard;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import cz.codetopic.utils.Objects;
import cz.codetopic.utils.R;
import cz.codetopic.utils.list.items.cardview.CardItem;

/**
 * Created by anty on 23.2.16.
 *
 * @author anty
 */
public abstract class DashboardItem implements CardItem {

    private final DashboardItemsAdapter mModule;
    private FrameLayout mContent;
    private ImageButton mHideButton;
    private ImageButton mRestoreButton;
    private boolean mEnabled = true;

    public DashboardItem(DashboardItemsAdapter module) {
        mModule = module;
    }

    @Override
    public final View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        if (oldView == null) {
            oldView = LayoutInflater.from(context).inflate(getLayoutResId(context, itemPosition), parent, false);
            mContent = (FrameLayout) oldView.findViewById(R.id.content_frame_layout);
            mHideButton = (ImageButton) oldView.findViewById(R.id.hide_image_button);
            mRestoreButton = (ImageButton) oldView.findViewById(R.id.restore_image_button);

            View.OnClickListener hideOnClick =
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onHideClick(v.getContext(), v);
                        }
                    };
            mHideButton.setOnClickListener(hideOnClick);
            mRestoreButton.setOnClickListener(hideOnClick);
        }

        if (mContent.getChildCount() == 0 || !Objects.equals(mContent.getTag(), getClass())) {
            mContent.removeAllViews();
            mContent.setTag(getClass());
            mContent.addView(onCreateView(context, mContent));
        }

        boolean show = isShowHideButton(), enabled = isEnabled();
        mHideButton.setVisibility(show ? (enabled ? View.VISIBLE : View.GONE) : View.GONE);
        mRestoreButton.setVisibility(show ? (enabled ? View.GONE : View.VISIBLE) : View.GONE);
        onUpdateView(context, mContent.getChildAt(0));

        return oldView;
    }

    public abstract View onCreateView(Context context, ViewGroup parent);

    public abstract void onUpdateView(Context context, View view);

    @Override
    public final int getLayoutResId(Context context, int itemPosition) {
        return R.layout.dashboard_item_base;
    }

    @Override
    public void onClick(Context context, View v, int itemPosition) {

    }

    @Override
    public boolean onLongClick(Context context, View v, int itemPosition) {
        return false;
    }

    public void onHideClick(Context context, View view) {
        setEnabled(!isEnabled());
    }

    public boolean isShowHideButton() {
        return true;
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            mModule.notifyItemsChanged();
        }
    }

    @Nullable
    public String getSaveName() {
        return null;
    }

    public abstract int getPriority();
}

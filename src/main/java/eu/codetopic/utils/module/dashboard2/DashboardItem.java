package eu.codetopic.utils.module.dashboard2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import eu.codetopic.utils.Objects;
import eu.codetopic.utils.R;
import eu.codetopic.utils.list.items.cardview.CardItem;

/**
 * Created by anty on 30.4.16.
 *
 * @author anty
 */
public abstract class DashboardItem implements CardItem {

    private final ItemData mData;
    private FrameLayout mContent;
    private ImageButton mHideButton;
    private ImageButton mRestoreButton;

    public DashboardItem(ItemData data) {
        mData = data;
    }

    /**
     * Returns broadcast actions for IntentFilter.
     * If any action is started, item view will be reloaded.
     *
     * @return broadcast actions for IntentFilter
     */
    @Deprecated
    public String[] getChangeIntentActions() {
        return new String[0];
    }

    public abstract int getPriority();

    public ItemData getData() {
        return mData;
    }

    public boolean isShowDescription() {
        return DashboardData.getter.get().isShowDescription();
    }

    @Override
    public final View getViewBase(Context context, ViewGroup parent, @Nullable View oldView, int itemPosition) {
        if (oldView == null) {
            oldView = LayoutInflater.from(context).inflate(getLayoutResId(context, itemPosition), parent, false);
            mContent = (FrameLayout) oldView.findViewById(R.id.content_frame_layout);
            mHideButton = (ImageButton) oldView.findViewById(R.id.hide_image_button);
            mRestoreButton = (ImageButton) oldView.findViewById(R.id.restore_image_button);

            View.OnClickListener onClickHideRestore =
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onHideRestoreClick(v.getContext(), v);
                        }
                    };
            mHideButton.setOnClickListener(onClickHideRestore);
            mRestoreButton.setOnClickListener(onClickHideRestore);
        }

        if (mContent.getChildCount() == 0 || !Objects.equals(mContent.getTag(), getClass())) {
            mContent.removeAllViews();
            mContent.addView(onCreateView(context, mContent, itemPosition));
            mContent.setTag(getClass());
        }

        boolean show = canBeUserDisabled(), enabled = mData.isUserEnabled();
        mHideButton.setVisibility(show && enabled ? View.VISIBLE : View.GONE);
        mRestoreButton.setVisibility(show && !enabled ? View.VISIBLE : View.GONE);
        onUpdateView(context, mContent.getChildAt(0), itemPosition);

        return oldView;
    }

    public abstract View onCreateView(Context context, ViewGroup parent, int position);

    public abstract void onUpdateView(Context context, View view, int position);

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

    public void onHideRestoreClick(Context context, View view) {
        mData.setUserEnabled(!mData.isUserEnabled());
    }

    public boolean canBeUserDisabled() {
        return false;
    }


    public abstract static class ItemData {

        private boolean userEnabled = true;

        public ItemData() {
            getDataSaver().restoreItemDataState(this);
        }

        private DashboardData getDataSaver() {
            return DashboardData.getter.get();
        }

        @Nullable
        public abstract String getSaveName();

        public boolean isUserEnabled() {
            return userEnabled;
        }

        public void setUserEnabled(boolean userEnabled) {
            if (this.userEnabled != userEnabled) {
                this.userEnabled = userEnabled;
                DashboardData data = getDataSaver();
                data.saveItemDataState(this);
                data.getContext().sendBroadcast(new Intent(
                        DashboardFragment.ACTION_NOTIFY_UPDATE_ITEMS));
            }
        }

        /**
         * @hide
         */
        final void setUserEnabledInternal(boolean userEnabled) {
            this.userEnabled = userEnabled;
        }

        public abstract boolean isEnabled();
    }

}

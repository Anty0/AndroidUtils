package cz.codetopic.utils.module.dashboard;

import android.content.Context;
import android.support.annotation.StringRes;

import cz.codetopic.utils.Constants;
import cz.codetopic.utils.Log;
import cz.codetopic.utils.R;
import cz.codetopic.utils.thread.JobUtils;

/**
 * Created by anty on 23.2.16.
 *
 * @author anty
 */
public abstract class DashboardItemsAdapter {

    private static final String LOG_TAG = "DashboardItemsAdapter";

    private final Context mContext;
    private boolean mInitialized = false;
    private boolean mInitializeStarted = false;
    private ModulesDashboardFragment mFragment = null;

    public DashboardItemsAdapter(Context context) {
        Log.d(LOG_TAG, "<init>");
        mContext = context;
    }

    @StringRes
    protected abstract CharSequence getName();

    public boolean isShowDescription() {
        Log.d(LOG_TAG, "isShowDescription");
        return mFragment.isShowDescription();
    }

    public synchronized final boolean isInitialized() {
        Log.d(LOG_TAG, "isInitialized");
        return mInitialized;
    }

    final synchronized void init(final DashboardData data, ModulesDashboardFragment fragment) {
        Log.d(LOG_TAG, "init");
        if (mInitializeStarted || mInitialized)
            throw new IllegalStateException(LOG_TAG + " is still initialized.");

        mInitializeStarted = true;
        mFragment = fragment;

        onInitialize(data);
    }

    protected void onInitialize(DashboardData data) {
        mInitialized = true;
        for (DashboardItem item : getItems())
            data.restoreDashboardItemEnabledState(item);

        notifyItemsChanged();
    }

    final synchronized void update(final DashboardData data) {
        Log.d(LOG_TAG, "update");
        if (!mInitialized)
            throw new IllegalStateException(LOG_TAG + " is not initialized.");

        onUpdate(data);
    }

    protected void onUpdate(DashboardData data) {

    }

    protected void onSaveState(DashboardData data) {
        for (DashboardItem item : getItems())
            data.saveDashboardItemEnabledState(item);
    }

    protected abstract DashboardItem[] getItems();

    protected DashboardItem getLoadingItem() {
        Log.d(LOG_TAG, "getLoadingItem");
        return new MultilineDashboardItem(this) {

            @Override
            public CharSequence getTitle(Context context, int position) {
                return getName();
            }

            @Override
            public CharSequence getText(Context context, int position) {
                return context.getText(R.string.wait_text_loading);
            }

            @Override
            protected int getMultilineLayoutRes(Context context) {
                return R.layout.listitem_multiline_loading;
            }

            @Override
            public boolean isShowHideButton() {
                return false;
            }

            @Override
            public int getPriority() {
                return Constants.DASHBOARD_ITEM_DEFAULT_PRIORITY_LOADING;
            }
        };
    }

    public Context getContext() {
        return mContext;
    }

    protected synchronized final void notifyItemsChanged() {
        Log.d(LOG_TAG, "notifyItemsChanged");
        if (isInitialized() && mFragment != null)
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mFragment.refreshItems();
                }
            });
    }

}

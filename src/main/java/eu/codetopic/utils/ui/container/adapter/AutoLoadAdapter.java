package eu.codetopic.utils.ui.container.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eu.codetopic.utils.Arrays;
import eu.codetopic.utils.R;
import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.log.Log;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.items.custom.CustomItemWrapper;
import eu.codetopic.utils.ui.container.items.custom.MultilineItemCustomItemWrapper;
import eu.codetopic.utils.ui.container.items.multiline.TextMultilineResourceLayoutItem;

public abstract class AutoLoadAdapter extends CustomItemAdapter<CustomItem> {

    private static final String LOG_TAG = "AutoLoadAdapter";
    private static final Object EDIT_TAG = new Object();//LOG_TAG + ".EDIT_TAG";

    private final Lock mSuspendLock = new ReentrantLock();
    private final Object mPageLock = new Object();
    private int mPage = getStartingPage();
    private boolean mEnabled = true;
    private boolean mShowLoadingItem = true;
    private CustomItem mLoadingItem;

    public AutoLoadAdapter(Context context) {
        super(context);
    }

    private CustomItem getLoadingItem() {
        if (mLoadingItem == null)
            mLoadingItem = generateLoadingItem();
        return mLoadingItem;
    }

    protected CustomItem generateLoadingItem() {
        CustomItemWrapper[] wrappers = new CustomItemWrapper[0];
        if (getBase() instanceof RecyclerView.Adapter<?>)
            wrappers = Arrays.add(wrappers, new CardViewWrapper());

        return new MultilineItemCustomItemWrapper(new
                TextMultilineResourceLayoutItem(getContext().getText(R.string.wait_text_loading),
                null, R.layout.item_multiline_loading), wrappers);
    }

    protected int getStartingPage() {
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            super.onBindViewHolder(holder, position);
        } finally {
            if (mShowLoadingItem && position == super.getItemCount())
                loadNextPage(false);
        }
    }

    public final void loadNextPage(boolean force) {
        if (mSuspendLock.tryLock()) {
            int page;
            synchronized (mPageLock) {
                page = mPage++;
            }
            final boolean firstPage = page == getStartingPage();
            final Editor<CustomItem> editor = edit();
            if (firstPage) editor.clear().notifyAllItemsChanged();
            final ActionCallback<Boolean> callback = new ActionCallback<Boolean>() {
                @Override
                public void onActionCompleted(@Nullable Boolean result, @Nullable Throwable caughtThrowable) {
                    editor.setTag(EDIT_TAG).apply();
                    AutoLoadAdapter adapter = editor.getAdapter();
                    if (adapter != null) {
                        adapter.setShowLoadingItem(result != null && result);
                        Base base;
                        if (firstPage && !(base = adapter.getBase()).hasOnlySimpleDataChangedReporting())
                            base.notifyDataSetChanged(); // first page auto scroll down fix
                        adapter.mSuspendLock.unlock();
                    }
                }
            };

            onLoadNextPage(page, editor, callback);
        } else if (force) {
            Log.d(LOG_TAG, "loadNextPage: still loading, trying to wait one loop before next try");
            JobUtils.postOnContextThread(getContext(), new Runnable() {
                @Override
                public void run() {
                    loadNextPage(true);
                }
            });
        }
    }

    protected abstract void onLoadNextPage(int page, Editor<CustomItem> editor,
                                           @NonNull ActionCallback<Boolean> callback);

    @Override
    public void onAttachToContainer(@Nullable Object container) {
        if (mPage == getStartingPage() && super.isEmpty()) loadNextPage(false);
        super.onAttachToContainer(container);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            if (mEnabled) reset();
            else setShowLoadingItem(false);
        }
    }

    public int getNextPage() {
        return mPage;
    }

    public void reset() {
        synchronized (mPageLock) {
            mPage = getStartingPage();
        }
        loadNextPage(true);
    }

    private void setShowLoadingItem(boolean show) {
        if (mShowLoadingItem != show) {
            mShowLoadingItem = show;
            if (show) getBase().notifyItemInserted(super.getItemCount());
            else getBase().notifyItemRemoved(super.getItemCount());
        }
    }

    @Override
    public CustomItem getItem(int position) {
        if (position == super.getItemCount())
            return getLoadingItem();
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        return mShowLoadingItem ? count + 1 : count;
    }

    @Override
    public int getItemPosition(CustomItem item) {
        if (item.equals(mLoadingItem)) return super.getItemCount();
        return super.getItemPosition(item);
    }

    @Override
    public CustomItem[] getItems(CustomItem[] contents) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public List<CustomItem> getItems() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isEmpty() {
        return !mShowLoadingItem && super.isEmpty();
    }

    @Override
    protected void assertAllowApplyChanges(@Nullable Object editTag, Collection<Modification<CustomItem>> modifications,
                                           @Nullable Collection<CustomItem> contentModifiedItems) {
        super.assertAllowApplyChanges(editTag, modifications, contentModifiedItems);
        if (EDIT_TAG != editTag) throw new UnsupportedOperationException(LOG_TAG +
                " can't be edited anytime, you can override method onLoadNextPage()");
    }

    @Override
    protected void onDataEdited(Object editorTag) {
        super.onDataEdited(editorTag);
        setShowLoadingItem(mEnabled);
    }
}

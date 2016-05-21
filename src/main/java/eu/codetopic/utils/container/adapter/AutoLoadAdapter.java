package eu.codetopic.utils.container.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.container.items.cardview.MultilineItemCardWrapper;
import eu.codetopic.utils.container.items.custom.CustomItem;
import eu.codetopic.utils.container.items.custom.MultilineItemCustomItemWrapper;
import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.container.items.multiline.TextMultilineResourceLayoutItem;
import eu.codetopic.utils.thread.JobUtils;

public abstract class AutoLoadAdapter extends CustomItemAdapter<CustomItem> {

    private static final String LOG_TAG = "AutoLoadAdapter";
    private static final Object EDIT_TAG = new Object();//LOG_TAG + ".EDIT_TAG";

    private final Lock mSuspendLock = new ReentrantLock();
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
        MultilineItem loadingItem = new TextMultilineResourceLayoutItem(getContext()
                .getText(R.string.wait_text_loading), null, R.layout.item_multiline_loading);
        return getBase() instanceof RecyclerView.Adapter ? new MultilineItemCardWrapper(loadingItem)
                : new MultilineItemCustomItemWrapper(loadingItem);
    }

    protected int getStartingPage() {
        return 0;
    }

    @Override
    public void onBindViewHolder(CustomItemViewHolder holder, int position) {
        try {
            super.onBindViewHolder(holder, position);
        } finally {
            if (mShowLoadingItem && position == super.getItemCount())
                loadNextPage(false);
        }
    }

    protected final void loadNextPage(boolean force) {
        if (mSuspendLock.tryLock()) {
            final Editor<CustomItem> editor = getEditor();
            if (mPage == getStartingPage()) editor.clear();
            ActionCallback<Boolean> callback = new ActionCallback<Boolean>() {
                @Override
                public void onActionCompleted(@Nullable Boolean result, @Nullable Throwable caughtThrowable) {
                    editor.setTag(EDIT_TAG).apply();
                    AutoLoadAdapter adapter = editor.getAdapter();
                    if (adapter != null) {
                        adapter.setShowLoadingItem(result != null && result);
                        adapter.mSuspendLock.unlock();
                    }
                }
            };

            onLoadNextPage(mPage++, editor, callback);
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

    public void reset() {
        mPage = getStartingPage();
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
    public boolean isEmpty() {
        return !mShowLoadingItem && super.isEmpty();
    }

    private Editor<CustomItem> getEditor() {
        return super.edit();
    }

    @Override
    public Editor<CustomItem> edit() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(LOG_TAG + " can't be edited anytime," +
                " you can override method onLoadNextPage()");
    }

    @Override
    public void postModifications(Collection<Modification<CustomItem>> modifications,
                                  @Nullable Collection<CustomItem> contentModifiedItems) {
        throw new UnsupportedOperationException(LOG_TAG + " can't be edited anytime," +
                " you can override method onLoadNextPage()");
    }

    @Override
    public void postModifications(@Nullable Object editTag, Collection<Modification<CustomItem>> modifications,
                                  @Nullable Collection<CustomItem> contentModifiedItems) {
        if (EDIT_TAG != editTag)
            throw new UnsupportedOperationException(LOG_TAG + " can't be edited anytime," +
                    " you can override method onLoadNextPage()");
        super.postModifications(editTag, modifications, contentModifiedItems);
    }

    @Override
    protected void onDataEdited(Object editorTag) {
        super.onDataEdited(editorTag);
        setShowLoadingItem(mEnabled);
    }
}

package eu.codetopic.utils.list.recyclerView.adapter;

import android.content.Context;

import java.util.Collection;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.R;
import eu.codetopic.utils.list.items.cardview.CardItem;
import eu.codetopic.utils.list.items.cardview.TextMultilineCardItem;

/**
 * Created by anty on 15.3.16.
 *
 * @author anty
 */
public class AutoLoadCardRecyclerAdapter extends CardRecyclerAdapter<CardItem> {

    private static final String LOG_TAG = "AutoLoadCardRecyclerAdapter";

    private int mPage = 0;
    private boolean mEnabled = false;
    private boolean mShowLoadingItem = false;
    private TextMultilineCardItem mLoadingItem;
    private OnLoadNextPageListener mOnLoadNextListListener;

    public AutoLoadCardRecyclerAdapter(Context context) {
        super(context);
        init(context);
    }

    public AutoLoadCardRecyclerAdapter(Context context, Collection<? extends CardItem> data) {
        super(context, data);
        init(context);
    }

    public AutoLoadCardRecyclerAdapter(Context context, CardItem... data) {
        super(context, data);
        init(context);
    }

    private void init(Context context) {
        mLoadingItem = new TextMultilineCardItem(context.getText(R.string.wait_text_loading),
                context.getText(R.string.wait_text_please_wait))
                .setLayoutResId(R.layout.listitem_multiline_loading);
    }

    public AutoLoadCardRecyclerAdapter setOnLoadNextListListener(OnLoadNextPageListener mOnLoadNextListListener) {
        this.mOnLoadNextListListener = mOnLoadNextListListener;
        return this;
    }

    @Override
    public void onBindViewHolder(CardViewViewHolder<CardItem> holder, int position) {
        try {
            super.onBindViewHolder(holder, position);
        } finally {
            if (mShowLoadingItem && position == super.getItemCount()) {
                setShowLoadingItem(false);
                mPage++;
                if (mOnLoadNextListListener != null)
                    mOnLoadNextListListener.onLoadNextPage(this, mPage);
            }
        }
    }

    @Override
    public CardItem getItem(int position) {
        Log.d(LOG_TAG, "getItem: " + position);
        if (position == super.getItemCount())
            return mLoadingItem;
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        Log.d(LOG_TAG, "getItemCount");
        int count = super.getItemCount();
        return mShowLoadingItem ? count + 1 : count;
    }

    public void reset() {
        mPage = 0;
        edit().clear().apply();
    }

    public AutoLoadCardRecyclerAdapter setEnabled(boolean enabled) {
        Log.d(LOG_TAG, "setEnabled: " + enabled);
        if (mEnabled != enabled) {
            mEnabled = enabled;
            if (mEnabled) reset();
            else setShowLoadingItem(false);
        }
        return this;
    }

    @Override
    protected void onDataEdited(Object editorTag) {
        super.onDataEdited(editorTag);
        setShowLoadingItem(mEnabled);
    }

    private void setShowLoadingItem(boolean show) {
        if (mShowLoadingItem != show) {
            mShowLoadingItem = show;
            if (show) notifyItemInserted(super.getItemCount());
            else notifyItemRemoved(super.getItemCount());
        }
    }

    public interface OnLoadNextPageListener {

        void onLoadNextPage(AutoLoadCardRecyclerAdapter adapter, int page);
    }
}

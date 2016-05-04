package eu.codetopic.utils.list.recyclerView.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;

import eu.codetopic.utils.Log;

/**
 * Created by anty on 28.9.15.
 *
 * @author anty
 */
public abstract class RecyclerAdapter<T, VH extends RecyclerAdapter
        .ItemViewHolder<T>> extends ArrayEditRecyclerAdapter<T, VH> {

    private static final String LOG_TAG = "RecyclerAdapter";

    private final int mLayoutResourceId;

    public RecyclerAdapter(int layoutResourceId) {
        super();
        Log.d(LOG_TAG, "<init> layoutResourceId: " + layoutResourceId);
        mLayoutResourceId = layoutResourceId;
    }

    public RecyclerAdapter(int layoutResourceId, Collection<? extends T> data) {
        super(data);
        Log.d(LOG_TAG, "<init> layoutResourceId: " + layoutResourceId);
        mLayoutResourceId = layoutResourceId;
    }

    @SafeVarargs
    public RecyclerAdapter(int layoutResourceId, T... data) {
        super(data);
        Log.d(LOG_TAG, "<init> layoutResourceId: " + layoutResourceId);
        mLayoutResourceId = layoutResourceId;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutResourceId, parent, false);

        return getViewHolderInstance(view);
    }

    protected abstract VH getViewHolderInstance(View view);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.onBindViewHolder(getItem(position), position);
    }

    public static abstract class ItemViewHolder<I> extends RecyclerView.ViewHolder {

        private static final String LOG_TAG = "ItemViewHolder";

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onBindViewHolder(I item, int position);
    }
}
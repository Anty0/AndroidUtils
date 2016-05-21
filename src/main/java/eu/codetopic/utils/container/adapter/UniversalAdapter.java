package eu.codetopic.utils.container.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import eu.codetopic.utils.R;
import eu.codetopic.utils.Utils;

public abstract class UniversalAdapter<VH extends RecyclerView.ViewHolder> {

    public static final int NO_LAYOUT_ID = 0;
    private static final String LOG_TAG = "UniversalAdapter";
    private Base base = null;

    public final void attachBase(@NonNull Base base) {
        if (isBaseAttached()) throw new IllegalStateException(LOG_TAG + " is still attached");
        this.base = base;
        onBaseAttached(this.base);
    }

    public final boolean isBaseAttached() {
        return base != null;
    }

    public final Base getBase() {
        if (!isBaseAttached()) throw new IllegalStateException(LOG_TAG + " is not attached");
        return base;
    }

    public final RecyclerBase<VH> forRecyclerView() {
        return new RecyclerBase<>(this);
    }

    public final ListBase<VH> forListView() {
        return new ListBase<>(this);
    }

    protected void onBaseAttached(Base base) {
    }

    public abstract VH onCreateViewHolder(ViewGroup parent, @LayoutRes int viewLayoutId);

    public abstract void onBindViewHolder(VH holder, int position);

    public abstract int getItemCount();

    public abstract Object getItem(int position);

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return false;
    }

    @LayoutRes
    public int getItemViewLayoutId(int position) {
        return NO_LAYOUT_ID;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void onBeforeRegisterDataObserver(Object observer) {
    }

    public void onAfterRegisterDataObserver(Object observer) {
    }

    public void onBeforeUnregisterDataObserver(Object observer) {
    }

    public void onAfterUnregisterDataObserver(Object observer) {
    }

    public interface Base {

        boolean hasObservers();

        boolean hasOnlySimpleDataChangedReporting();

        void notifyDataSetChanged();

        void notifyItemChanged(int position);

        void notifyItemRangeChanged(int positionStart, int itemCount);

        void notifyItemInserted(int position);

        void notifyItemMoved(int fromPosition, int toPosition);

        void notifyItemRangeInserted(int positionStart, int itemCount);

        void notifyItemRemoved(int position);

        void notifyItemRangeRemoved(int positionStart, int itemCount);
    }

    public static abstract class SimpleReportingBase implements Base {

        @Override
        public boolean hasOnlySimpleDataChangedReporting() {
            return true;
        }

        @Override
        public void notifyItemChanged(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyItemInserted(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyItemMoved(int fromPosition, int toPosition) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyItemRemoved(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            notifyDataSetChanged();
        }
    }

    public static class ListBase<VH extends RecyclerView.ViewHolder>
            extends SimpleReportingBase implements ListAdapter, SpinnerAdapter {

        private final UniversalAdapter<VH> mAdapter;
        private final DataObservable mObservable = new DataObservable();

        public ListBase(UniversalAdapter<VH> adapter) {
            mAdapter = adapter;
            mAdapter.attachBase(this);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.onBeforeRegisterDataObserver(observer);
            mObservable.registerObserver(observer);
            mAdapter.onAfterRegisterDataObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.onBeforeUnregisterDataObserver(observer);
            mObservable.unregisterObserver(observer);
            mAdapter.onAfterUnregisterDataObserver(observer);
        }

        @Override
        public boolean hasObservers() {
            return mObservable.hasObservers();
        }

        @Override
        public void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.frame_wrapper_base, parent, false);

            ViewGroup itemParent = (ViewGroup) convertView;
            View view = itemParent.getChildCount() == 1 ? itemParent.getChildAt(0) : null;
            int viewType = mAdapter.getItemViewLayoutId(position);
            VH viewHolder;
            if (view == null) {
                viewHolder = mAdapter.onCreateViewHolder(itemParent, viewType);
                view = viewHolder.itemView;
                itemParent.addView(view);
                itemParent.setTag(viewHolder);
            } else {
                //noinspection unchecked
                viewHolder = (VH) itemParent.getTag();
                if (viewHolder.getItemViewType() != viewType)
                    return getView(position, null, parent);
            }
            mAdapter.onBindViewHolder(viewHolder, position);
            Utils.copyLayoutParamsSizesToView(itemParent, view.getLayoutParams());
            //Log.d(LOG_TAG, "ListBase getView:\n" + Utils.drawViewHierarchy(convertView, false, true));
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return mAdapter.getItemCount();
        }

        @Override
        public Object getItem(int position) {
            return mAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter.hasStableIds();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return mAdapter.isEmpty();
        }

        private static class DataObservable extends DataSetObservable {

            public boolean hasObservers() {
                return !mObservers.isEmpty();
            }
        }
    }

    public static class RecyclerBase<VH extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<VH> implements Base {

        private final UniversalAdapter<VH> mAdapter;

        public RecyclerBase(UniversalAdapter<VH> adapter) {
            mAdapter = adapter;
            mAdapter.attachBase(this);
            setHasStableIds(mAdapter.hasStableIds());
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            mAdapter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return mAdapter.getItemCount();
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mAdapter.getItemViewLayoutId(position);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            mAdapter.onBeforeRegisterDataObserver(observer);
            super.registerAdapterDataObserver(observer);
            mAdapter.onAfterRegisterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            mAdapter.onBeforeUnregisterDataObserver(observer);
            super.unregisterAdapterDataObserver(observer);
            mAdapter.onAfterUnregisterDataObserver(observer);
        }

        @Override
        public boolean hasOnlySimpleDataChangedReporting() {
            return false;
        }
    }
}

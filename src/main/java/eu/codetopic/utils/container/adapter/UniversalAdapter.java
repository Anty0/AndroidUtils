package eu.codetopic.utils.container.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import eu.codetopic.utils.Objects;
import eu.codetopic.utils.Utils;

public abstract class UniversalAdapter<VH extends UniversalAdapter.ViewHolder> {

    public static final int NO_VIEW_TYPE = 0;
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

    public final RecyclerView.Adapter<?> forRecyclerView() {
        return new RecyclerBase<>(this);
    }

    public final ListAdapter forListView() {
        return new ListBase<>(this);
    }

    public final SpinnerAdapter forSpinner() {
        return new ListBase<>(this);
    }

    protected void onBaseAttached(Base base) {
    }

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

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
    public int getItemViewType(int position) {
        return NO_VIEW_TYPE;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void onAttachToContainer(@Nullable Object container) {
    }

    public void onDetachFromContainer(@Nullable Object container) {
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

    public static class ViewHolder {

        public final View itemView;
        public final int viewType;

        public ViewHolder(View itemView, int viewType) {
            this.itemView = itemView;
            this.viewType = viewType;
        }
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

    public static class ListBase<VH extends ViewHolder>
            extends SimpleReportingBase implements ListAdapter, SpinnerAdapter {

        private static final String LOG_TAG = UniversalAdapter.LOG_TAG + "$ListBase";
        private static final String VIEW_TAG_KEY_VIEW_HOLDER = LOG_TAG + ".VIEW_HOLDER";

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
            if (!mObservable.hasObservers()) mAdapter.onAttachToContainer(null);

            mAdapter.onBeforeRegisterDataObserver(observer);
            mObservable.registerObserver(observer);
            mAdapter.onAfterRegisterDataObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.onBeforeUnregisterDataObserver(observer);
            mObservable.unregisterObserver(observer);
            mAdapter.onAfterUnregisterDataObserver(observer);

            if (!mObservable.hasObservers()) mAdapter.onDetachFromContainer(null);
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
            //noinspection unchecked
            VH viewHolder = convertView != null ? (VH) Utils.getViewTag(convertView,
                    VIEW_TAG_KEY_VIEW_HOLDER) : null;
            int requestedViewType = mAdapter.getItemViewType(position);
            if (viewHolder == null || !Objects.equals(requestedViewType, viewHolder.viewType)) {
                viewHolder = mAdapter.onCreateViewHolder(parent, requestedViewType);
                if (viewHolder.viewType != requestedViewType)
                    throw new IllegalArgumentException("ViewHolder returned by " +
                            "UniversalAdapter.onCreateViewHolder() has invalid viewType.");
                convertView = viewHolder.itemView;
                Utils.setViewTag(convertView, VIEW_TAG_KEY_VIEW_HOLDER, viewHolder);
            }
            mAdapter.onBindViewHolder(viewHolder, position);
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

    public static class RecyclerBase<VH extends ViewHolder>
            extends RecyclerView.Adapter<RecyclerBase.UniversalViewHolder<VH>> implements Base {

        private final UniversalAdapter<VH> mAdapter;

        public RecyclerBase(UniversalAdapter<VH> adapter) {
            mAdapter = adapter;
            mAdapter.attachBase(this);
            setHasStableIds(mAdapter.hasStableIds());
        }

        @Override
        public UniversalViewHolder<VH> onCreateViewHolder(ViewGroup parent, int viewType) {
            VH result = mAdapter.onCreateViewHolder(parent, viewType);
            if (result.viewType != viewType)
                throw new IllegalArgumentException("ViewHolder returned by " +
                        "UniversalAdapter.onCreateViewHolder() has invalid viewType.");
            return new UniversalViewHolder<>(result);
        }

        @Override
        public void onBindViewHolder(UniversalViewHolder<VH> holder, int position) {
            mAdapter.onBindViewHolder(holder.universalHolder, position);
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
            return mAdapter.getItemViewType(position);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            mAdapter.onAttachToContainer(recyclerView);
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mAdapter.onDetachFromContainer(recyclerView);
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

        protected static class UniversalViewHolder<VH extends ViewHolder> extends RecyclerView.ViewHolder {

            public final VH universalHolder;

            public UniversalViewHolder(VH universalHolder) {
                super(universalHolder.itemView);
                this.universalHolder = universalHolder;
            }
        }
    }
}

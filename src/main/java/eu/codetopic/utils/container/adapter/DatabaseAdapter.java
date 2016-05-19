package eu.codetopic.utils.container.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.container.items.cardview.MultilineItemCardWrapper;
import eu.codetopic.utils.container.items.custom.CustomItem;
import eu.codetopic.utils.container.items.multiline.MultilineItem;
import eu.codetopic.utils.data.database.DatabaseObjectChangeDetector;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.DatabaseJob;

public class DatabaseAdapter<T, ID> extends CustomItemAdapter<CustomItem> {

    private static final String LOG_TAG = "DatabaseAdapter";
    private static final String EDIT_TAG = LOG_TAG + ".EDIT_TAG";

    private final ItemsGetter<T, ID> mItemsGetter;
    private final BroadcastReceiver mDataChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyDatabaseDataChanged();
        }
    };

    public DatabaseAdapter(Context context, DatabaseDaoGetter<T> daoGetter,
                           @Nullable LoadingViewHolder viewHolder) {
        this(context, new DefaultItemsGetter<T, ID>(daoGetter, viewHolder));
    }

    public DatabaseAdapter(Context context, @NonNull ItemsGetter<T, ID> itemsGetter) {
        super(context);
        itemsGetter.attach(this);
        mItemsGetter = itemsGetter;
    }

    public synchronized void notifyDatabaseDataChanged() {
        mItemsGetter.onUpdateItems(getContext());
    }

    @Override
    public void onRegisterDataObserver(Object observer) {
        if (!getBase().hasObservers()) {
            getContext().registerReceiver(mDataChangedReceiver, mItemsGetter
                    .getDatabaseItemsChangedIntentFilter(getContext()));
            notifyDatabaseDataChanged();
        }
        super.onRegisterDataObserver(observer);
    }

    @Override
    public void onUnregisterDataObserver(Object observer) {
        super.onUnregisterDataObserver(observer);
        if (!getBase().hasObservers())
            getContext().unregisterReceiver(mDataChangedReceiver);
    }

    @Override
    public Editor<CustomItem, DatabaseAdapter<T, ID>> edit() {
        throw new UnsupportedOperationException(LOG_TAG + " don't support external editing," +
                " you can call notifyDatabaseDataChanged() if you want to force refresh " + LOG_TAG + " content");
    }

    @Override
    public void postModifications(@Nullable Object editTag, Collection<Modification<CustomItem>>
            modifications, Collection<CustomItem> contentModifiedItems) {
        if (!EDIT_TAG.equals(editTag))
            throw new UnsupportedOperationException(LOG_TAG + " don't support external editing," +
                    " you can call notifyDatabaseDataChanged() if you want to force refresh " + LOG_TAG + " content");
        super.postModifications(editTag, modifications, contentModifiedItems);
    }

    @Override
    public void postModifications(Collection<Modification<CustomItem>> modifications, Collection<CustomItem> contentModifiedItems) {
        throw new UnsupportedOperationException(LOG_TAG + " don't support external editing," +
                " you can call notifyDatabaseDataChanged() if you want to force refresh " + LOG_TAG + " content");
    }

    private synchronized void setItems(@NonNull Collection<? extends CustomItem> items) {
        super.edit().clear().addAll(items).notifyAllItemsChanged().setTag(EDIT_TAG).apply();
    }

    public static class FilteredItemsGetter<T, ID> extends DefaultItemsGetter<T, ID> {

        private Filter<T> mFilter = null;
        private PreparedQuery<T> mPreparedQuery = null;
        private Comparator<T> mComparator = null;

        public FilteredItemsGetter(DatabaseDaoGetter<T> daoGetter,
                                   @Nullable LoadingViewHolder viewHolder) {
            super(daoGetter, viewHolder);
        }

        public FilteredItemsGetter<T, ID> setFilter(Filter<T> filter) {
            this.mFilter = filter;
            return this;
        }

        public FilteredItemsGetter<T, ID> setQuery(PreparedQuery<T> query) {
            this.mPreparedQuery = query;
            return this;
        }

        public FilteredItemsGetter<T, ID> setSorter(Comparator<T> comparator) {
            this.mComparator = comparator;
            return this;
        }

        @Override
        @WorkerThread
        protected Collection<? extends T> getItems(Dao<T, ID> dao) throws Throwable {
            List<? extends T> items = getDatabaseItems(dao);
            if (mComparator != null) Collections.sort(items, mComparator);
            return items;
        }

        @WorkerThread
        protected List<? extends T> getDatabaseItems(Dao<T, ID> dao) throws Throwable {
            if (mFilter == null)
                return mPreparedQuery != null ? dao.query(mPreparedQuery) : dao.queryForAll();

            List<T> items = new ArrayList<>();
            CloseableWrappedIterable<T> iterable = mPreparedQuery != null ?
                    dao.getWrappedIterable(mPreparedQuery) : dao.getWrappedIterable();
            for (CloseableIterator<T> iterator = iterable
                    .closeableIterator(); iterator.hasNext(); ) {
                T item = iterator.nextThrow();
                if (mFilter.use(item)) items.add(item);
            }
            iterable.close();
            return items;
        }

        public interface Filter<T> {
            boolean use(T item);
        }
    }

    public static class DefaultItemsGetter<T, ID> extends ItemsGetter<T, ID> {

        private final DatabaseDaoGetter<T> mDaoGetter;
        private final LoadingViewHolder mViewHolder;

        public DefaultItemsGetter(DatabaseDaoGetter<T> daoGetter,
                                  @Nullable LoadingViewHolder viewHolder) {
            mDaoGetter = daoGetter;
            mViewHolder = viewHolder;
        }

        public DatabaseDaoGetter<T> getDaoGetter() {
            return mDaoGetter;
        }

        public LoadingViewHolder getViewHolder() {
            return mViewHolder;
        }

        @WorkerThread
        protected Collection<? extends T> getItems(Dao<T, ID> dao) throws Throwable {
            return dao.queryForAll();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onUpdateItems(Context context) {
            DatabaseJob.start(mViewHolder, mDaoGetter, new DatabaseJob.DatabaseWork<T, ID>() {
                @Override
                public void run(Dao<T, ID> dao) throws Throwable {
                    Class<T> dataClass = dao.getDataClass();
                    if (CustomItem.class.isAssignableFrom(dataClass)) {
                        setItems((List<? extends CustomItem>) getItems(dao));
                    } else if (MultilineItem.class.isAssignableFrom(dataClass)) {
                        setItems(MultilineItemCardWrapper
                                .wrapAll((Collection<? extends MultilineItem>) getItems(dao)));
                    } else {
                        setItems(Collections.<CustomItem>emptyList());
                        Log.e(LOG_TAG, "notifyDatabaseDataChanged problem detected:" +
                                " database data class don't implements CustomItem or MultilineItem," +
                                " so " + LOG_TAG + " will be empty");
                    }
                }
            });
        }

        @Override
        public IntentFilter getDatabaseItemsChangedIntentFilter(Context context) {
            return DatabaseObjectChangeDetector.getIntentFilterObjectChanged(mDaoGetter.getDaoObjectClass());
        }
    }

    public abstract static class ItemsGetter<T, ID> {

        private DatabaseAdapter<T, ID> mAdapter = null;

        private void attach(DatabaseAdapter<T, ID> adapter) {
            if (mAdapter != null) throw new IllegalStateException("ItemsGetter is still attached");
            mAdapter = adapter;
        }

        protected abstract void onUpdateItems(Context context);

        protected final void setItems(@NonNull final Collection<? extends CustomItem> items) {
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setItems(items);
                }
            });
        }

        protected void requestUpdateItems() {
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDatabaseDataChanged();
                }
            });
        }

        protected abstract IntentFilter getDatabaseItemsChangedIntentFilter(Context context);
    }
}

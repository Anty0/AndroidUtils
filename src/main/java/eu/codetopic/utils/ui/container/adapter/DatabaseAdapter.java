/*
 * Copyright 2017 Jiří Kuchyňka (Anty)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.codetopic.utils.ui.container.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.LocalBroadcastManager;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.codetopic.java.utils.log.Log;
import eu.codetopic.utils.data.database.DatabaseObjectChangeDetector;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.database.DatabaseWork;
import eu.codetopic.utils.thread.job.database.DbJob;
import eu.codetopic.utils.ui.container.items.custom.CardViewWrapper;
import eu.codetopic.utils.ui.container.items.custom.CustomItem;
import eu.codetopic.utils.ui.container.items.custom.MultilineItemCustomItemWrapper;
import eu.codetopic.utils.ui.container.items.multiline.MultilineItem;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

public class DatabaseAdapter<T, ID> extends CustomItemAdapter<CustomItem> {

    private static final String LOG_TAG = "DatabaseAdapter";
    private static final Object EDIT_TAG = new Object();//LOG_TAG + ".EDIT_TAG";

    private final ItemsGetter<T, ID> mItemsGetter;
    private final BroadcastReceiver mDataChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyDatabaseDataChanged();
        }
    };

    public DatabaseAdapter(Context context, DatabaseDaoGetter<T, ID> daoGetter,
                           @Nullable LoadingVH viewHolder) {
        this(context, new DefaultItemsGetter<>(daoGetter, viewHolder));
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
    public void onAttachToContainer(@Nullable Object container) {
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(mDataChangedReceiver, mItemsGetter
                        .getDatabaseItemsChangedIntentFilter(getContext()));
        notifyDatabaseDataChanged();
        super.onAttachToContainer(container);
    }

    @Override
    public void onDetachFromContainer(@Nullable Object container) {
        super.onDetachFromContainer(container);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mDataChangedReceiver);
    }

    @Override
    protected void assertAllowApplyChanges(@Nullable Object editTag, Collection<Modification<CustomItem>> modifications,
                                           @Nullable Collection<CustomItem> contentModifiedItems) {
        super.assertAllowApplyChanges(editTag, modifications, contentModifiedItems);
        if (EDIT_TAG != editTag)
            throw new UnsupportedOperationException(LOG_TAG + " don't support external editing," +
                    " you can call notifyDatabaseDataChanged() if you want to force refresh " + LOG_TAG + " content");
    }

    private synchronized void setItems(@NonNull Collection<? extends CustomItem> items) {
        edit().clear().addAll(items).notifyAllItemsChanged().setTag(EDIT_TAG).apply();
    }

    public static class FilteredItemsGetter<T, ID> extends DefaultItemsGetter<T, ID> {

        private Filter<T> mFilter = null;
        private PreparedQuery<T> mPreparedQuery = null;
        private Comparator<T> mComparator = null;

        public FilteredItemsGetter(DatabaseDaoGetter<T, ID> daoGetter,
                                   @Nullable LoadingVH viewHolder) {
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

        private final DatabaseDaoGetter<T, ID> mDaoGetter;
        private final LoadingVH mViewHolder;

        public DefaultItemsGetter(DatabaseDaoGetter<T, ID> daoGetter,
                                  @Nullable LoadingVH viewHolder) {
            mDaoGetter = daoGetter;
            mViewHolder = viewHolder;
        }

        public DatabaseDaoGetter<T, ID> getDaoGetter() {
            return mDaoGetter;
        }

        public LoadingVH getViewHolder() {
            return mViewHolder;
        }

        @WorkerThread
        protected Collection<? extends T> getItems(Dao<T, ID> dao) throws Throwable {
            return dao.queryForAll();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onUpdateItems(Context context) {
            DbJob.work(mDaoGetter).withLoading(mViewHolder).start(new DatabaseWork<T, ID>() {
                @Override
                public void run(Dao<T, ID> dao) throws Throwable {
                    Class<T> dataClass = dao.getDataClass();
                    if (CustomItem.class.isAssignableFrom(dataClass)) {
                        setItems((List<? extends CustomItem>) getItems(dao));
                    } else if (MultilineItem.class.isAssignableFrom(dataClass)) {
                        setItems(MultilineItemCustomItemWrapper
                                .wrapAll((Collection<? extends MultilineItem>) getItems(dao),
                                        new CardViewWrapper()));
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
            JobUtils.runOnContextThread(mAdapter.getContext(), new Runnable() {
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

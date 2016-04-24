package eu.codetopic.utils.list.recyclerView.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.j256.ormlite.dao.Dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.codetopic.utils.Log;
import eu.codetopic.utils.activity.loading.LoadingViewHolder;
import eu.codetopic.utils.database.DatabaseObjectChangeDetector;
import eu.codetopic.utils.list.items.cardview.CardItem;
import eu.codetopic.utils.list.items.cardview.MultilineItemCardWrapper;
import eu.codetopic.utils.list.items.multiline.MultilineItem;
import eu.codetopic.utils.module.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.DatabaseJob;

/**
 * Created by anty on 30.3.16.
 *
 * @author anty
 */
public class DatabaseCardRecyclerAdapter<T, ID> extends CardRecyclerAdapter<CardItem> {

    private static final String LOG_TAG = "DatabaseCardRecyclerAdapter";

    private final ItemsGetter<T, ID> mItemsGetter;
    private final BroadcastReceiver mDataChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyDatabaseDataChanged();
        }
    };
    private boolean mRegistered = false;

    public DatabaseCardRecyclerAdapter(Context context, DatabaseDaoGetter<T> daoGetter,
                                       @Nullable LoadingViewHolder viewHolder) {
        this(context, new DefaultItemsGetter<T, ID>(daoGetter, viewHolder));
    }

    public DatabaseCardRecyclerAdapter(Context context, @NonNull ItemsGetter<T, ID> itemsGetter) {
        super(context);
        itemsGetter.attach(this);
        mItemsGetter = itemsGetter;
    }

    public void notifyDatabaseDataChanged() {
        mItemsGetter.onUpdateItems(getContext());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (!mRegistered) {
            getContext().registerReceiver(mDataChangedReceiver, mItemsGetter
                    .getDatabaseItemsChangedIntentFilter(getContext()));
            mRegistered = true;
            notifyDatabaseDataChanged();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (!mRegistered) Log.e(LOG_TAG, "onDetachedFromRecyclerView problem detected:" +
                " mDataChangedReceiver is not registered while onDetachedFromRecyclerView() is called");
        else if (!hasObservers()) {
            getContext().unregisterReceiver(mDataChangedReceiver);
            mRegistered = false;
        }
    }

    @Override
    public Editor<CardItem> edit() {
        throw new UnsupportedOperationException(LOG_TAG + " don't support external editing," +
                " you can call notifyDatabaseDataChanged() if you want to force refresh " + LOG_TAG + " content");
    }

    private void setItems(Collection<? extends CardItem> items) {
        super.edit().clear().addAll(items).apply();
    }

    public static class DefaultItemsGetter<T, ID> extends ItemsGetter<T, ID> {

        private final DatabaseDaoGetter<T> mDaoGetter;
        private final LoadingViewHolder mViewHolder;

        public DefaultItemsGetter(DatabaseDaoGetter<T> daoGetter,
                                  @Nullable LoadingViewHolder viewHolder) {
            mDaoGetter = daoGetter;
            mViewHolder = viewHolder;
        }

        @Override
        public void onUpdateItems(Context context) {
            DatabaseJob.start(mViewHolder, mDaoGetter, new DatabaseJob.DatabaseWork<T, ID>() {
                @Override
                public void run(Dao<T, ID> dao) throws Throwable {
                    Class<T> dataClass = mDaoGetter.getDaoObjectClass();
                    if (CardItem.class.isAssignableFrom(dataClass)) {
                        //noinspection unchecked
                        setItems((List<? extends CardItem>) dao.queryForAll());
                    } else if (MultilineItem.class.isAssignableFrom(dataClass)) {
                        //noinspection unchecked
                        setItems(MultilineItemCardWrapper
                                .wrapAll((Collection<? extends MultilineItem>) dao.queryForAll()));
                    } else {
                        setItems(Collections.<CardItem>emptyList());
                        Log.e(LOG_TAG, "notifyDatabaseDataChanged problem detected:" +
                                " database data class don't implements CardItem or MultilineItem," +
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

        private DatabaseCardRecyclerAdapter<T, ID> mAdapter = null;

        private void attach(DatabaseCardRecyclerAdapter<T, ID> adapter) {
            if (mAdapter != null) throw new IllegalStateException("ItemsGetter is still attached");
            mAdapter = adapter;
        }

        protected abstract void onUpdateItems(Context context);

        protected final void setItems(final Collection<? extends CardItem> items) {
            JobUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setItems(items);
                }
            });
        }

        protected abstract IntentFilter getDatabaseItemsChangedIntentFilter(Context context);
    }
}

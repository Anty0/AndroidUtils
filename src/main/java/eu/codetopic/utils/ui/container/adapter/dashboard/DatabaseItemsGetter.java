package eu.codetopic.utils.ui.container.adapter.dashboard;

import android.content.Context;

import java.util.Collection;
import java.util.List;

import eu.codetopic.utils.callback.ActionCallback;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.job.database.DbJob;

public abstract class DatabaseItemsGetter<T, ID> extends LoadableItemsGetterImpl
        implements DbJob.CallbackWork<List<? extends ItemInfo>, T, ID> {

    private static final String LOG_TAG = "DatabaseItemsGetter";

    private final DatabaseDaoGetter<T, ID> daoGetter;

    public DatabaseItemsGetter(DatabaseDaoGetter<T, ID> daoGetter) {
        this.daoGetter = daoGetter;
    }

    @Override
    protected final void loadItems(Context context, final ActionCallback<Collection<? extends ItemInfo>> callback) {
        DbJob.work(daoGetter).startCallback(this, new DbJob.Callback<List<? extends ItemInfo>>() {
            @Override
            public void onResult(List<? extends ItemInfo> result) {
                callback.onActionCompleted(result, null);
            }

            @Override
            public void onException(Throwable t) {
                callback.onActionCompleted(null, t);
            }
        });
    }
}

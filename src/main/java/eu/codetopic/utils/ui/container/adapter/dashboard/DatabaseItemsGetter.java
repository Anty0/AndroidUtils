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

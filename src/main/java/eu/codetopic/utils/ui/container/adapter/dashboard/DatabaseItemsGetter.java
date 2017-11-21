/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
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

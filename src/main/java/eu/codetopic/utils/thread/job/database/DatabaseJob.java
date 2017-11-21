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

package eu.codetopic.utils.thread.job.database;

import android.support.annotation.Nullable;
import android.widget.Toast;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Params;

import eu.codetopic.utils.Constants;
import eu.codetopic.utils.R;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import eu.codetopic.utils.thread.JobUtils;
import eu.codetopic.utils.thread.job.LoadingJob;
import eu.codetopic.utils.ui.view.holder.loading.LoadingVH;

public class DatabaseJob<T, ID> extends LoadingJob {

    private static final String LOG_TAG = "DatabaseJob";
    private static final String JOB_DATABASE_GROUP_NAME_ADD = ".DATABASE_GROUP";
    private final DatabaseDaoGetter<T, ID> daoGetter;
    private final DatabaseWork<T, ID> job;

    public DatabaseJob(@Nullable LoadingVH loadingViewHolder,
                       DatabaseDaoGetter<T, ID> daoGetter, DatabaseWork<T, ID> job) {
        super(new Params(Constants.JOB_PRIORITY_DATABASE)
                .groupBy(generateDatabaseJobGroupNameFor(daoGetter
                        .getDaoObjectClass())), loadingViewHolder);
        this.daoGetter = daoGetter;
        this.job = job;
    }

    public static String generateDatabaseJobGroupNameFor(Class<?> databaseObject) {
        return databaseObject.getName() + JOB_DATABASE_GROUP_NAME_ADD;
    }

    @Override
    public void onStart() throws Throwable {
        job.run(daoGetter.get());
    }

    @Override
    protected int getRetryLimit() {
        return Constants.JOB_REPEAT_COUNT_DATABASE;// don't change!! (otherwise it cause lot's of problems)
    }

    @Override
    protected void onCancel(@CancelReason int cancelReason, Throwable throwable) {
        super.onCancel(cancelReason, throwable);
        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.toast_text_database_exception,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

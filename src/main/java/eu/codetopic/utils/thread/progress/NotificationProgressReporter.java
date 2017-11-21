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

package eu.codetopic.utils.thread.progress;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import eu.codetopic.utils.thread.JobUtils;

public class NotificationProgressReporter extends ProgressReporterImpl {

    private final int mId;
    private final Context mContext;
    private final NotificationCompat.Builder mNotification;

    public NotificationProgressReporter(Context context, NotificationCompat.Builder notification, int id) {
        mId = id;
        mContext = context.getApplicationContext();
        mNotification = notification;
        mNotification.setOngoing(true).setOnlyAlertOnce(true)
                .setProgress(getMax(), getProgress(), isIntermediate());
    }

    public int getId() {
        return mId;
    }

    public Context getContext() {
        return mContext;
    }

    public NotificationCompat.Builder getNotification() {
        return mNotification;
    }

    @Override
    protected void onChange(ProgressInfo info) {
        mNotification.setProgress(info.getMaxProgress(),
                info.getProgress(), info.isIntermediate());

        JobUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                ((NotificationManager) mContext.getSystemService(Context
                        .NOTIFICATION_SERVICE)).notify(mId, mNotification.build());
            }
        });
    }
}

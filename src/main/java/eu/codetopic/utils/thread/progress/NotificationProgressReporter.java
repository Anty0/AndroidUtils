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
